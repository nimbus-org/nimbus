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
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.io.FileNotFoundException;
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
    
    protected String keyGeneratorProviderName;
    protected Provider keyGeneratorProvider;
    protected AlgorithmParameterSpec keyGeneratorAlgorithmParameterSpec;
    
    protected String keyFactoryProviderName;
    protected Provider keyFactoryProvider;
    
    protected int keySize = DEFAULT_KEY_LENGTH;
    protected byte[] iv;
    protected int ivLength;
    
    protected String secretKeyAlgorithm = DEFAULT_SECRET_KEY_ALGORITHM;
    protected byte[] secretKeyBytes;
    protected String secretKeyFile;
    protected Key key;
    
    protected String keyPairAlgorithm;
    protected byte[] publicKeyBytes;
    protected String publicKeyFile;
    protected byte[] privateKeyBytes;
    protected String privateKeyFile;
    protected PrivateKey privateKey;
    protected PublicKey publicKey;
    
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
    protected AlgorithmParameterSpec signatureAlgorithmParameterSpec;
    
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
    
    protected boolean isCheckOnStart = true;
    
    protected boolean isCreateKey;
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
    public void setSecretKeyAlgorithm(String algorithm){
        secretKeyAlgorithm = algorithm;
    }
    // CipherCryptServiceMBean のJavaDoc
    public String getSecretKeyAlgorithm(){
        return secretKeyAlgorithm;
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
    
    // CipherCryptServiceMBean のJavaDoc
    public void setIV(byte[] iv){
        this.iv = iv;
    }
    
    // CipherCryptServiceMBean のJavaDoc
    public byte[] getIV(){
        if(iv == null && algorithmParameters != null && ivLength <= 0){
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
    
    // CipherCryptServiceMBean のJavaDoc
    public void setIVString(String iv){
        setIV(iv == null ? null : toBytes(iv));
    }
    
    // CipherCryptServiceMBean のJavaDoc
    public String getIVString(){
        byte[] iv = getIV();
        return iv == null ? null : toString(iv);
    }
    
    public void setIVLength(int length){
        ivLength = length;
    }
    public int getIVLength(){
        return ivLength;
    }
    
    // CipherCryptServiceMBean のJavaDoc
    public void setSecretKeyBytes(byte[] bytes){
        secretKeyBytes = bytes;
    }
    // CipherCryptServiceMBean のJavaDoc
    public byte[] getSecretKeyBytes(){
        return secretKeyBytes;
    }
    
    // CipherCryptServiceMBean のJavaDoc
    public void setSecretKeyString(String str){
        setSecretKeyBytes(str == null ? null : toBytes(str));
    }
    // CipherCryptServiceMBean のJavaDoc
    public String getSecretKeyString(){
        return secretKeyBytes == null ? null : toString(secretKeyBytes);
    }
    
    // CipherCryptServiceMBean のJavaDoc
    public void setSecretKeyFile(String path){
        secretKeyFile = path;
    }
    // CipherCryptServiceMBean のJavaDoc
    public String getSecretKeyFile(){
        return secretKeyFile;
    }
    
    /**
     * 鍵を設定する。<p>
     *
     * @param k 鍵
     */
    public void setKey(Key k){
        key = k;
        if(key != null){
            if(key instanceof SecretKey){
                secretKeyAlgorithm = key.getAlgorithm();
            }
            isCreateKey = false;
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
    public void setPublicKeyBytes(byte[] bytes){
        publicKeyBytes = bytes;
    }
    // CipherCryptServiceMBean のJavaDoc
    public byte[] getPublicKeyBytes(){
        return publicKeyBytes;
    }
    
    // CipherCryptServiceMBean のJavaDoc
    public void setPublicKeyString(String str){
        setPublicKeyBytes(str == null ? null : toBytes(str));
    }
    // CipherCryptServiceMBean のJavaDoc
    public String getPublicKeyString(){
        return publicKeyBytes == null ? null : toString(publicKeyBytes);
    }
    
    // CipherCryptServiceMBean のJavaDoc
    public void setPublicKeyFile(String path){
        publicKeyFile = path;
    }
    // CipherCryptServiceMBean のJavaDoc
    public String getPublicKeyFile(){
        return publicKeyFile;
    }
    
    // CipherCryptServiceMBean のJavaDoc
    public void setPrivateKeyBytes(byte[] bytes){
        privateKeyBytes = bytes;
    }
    // CipherCryptServiceMBean のJavaDoc
    public byte[] getPrivateKeyBytes(){
        return privateKeyBytes;
    }
    
    // CipherCryptServiceMBean のJavaDoc
    public void setPrivateKeyString(String str){
        setPrivateKeyBytes(str == null ? null : toBytes(str));
    }
    // CipherCryptServiceMBean のJavaDoc
    public String getPrivateKeyString(){
        return privateKeyBytes == null ? null : toString(privateKeyBytes);
    }
    
    // CipherCryptServiceMBean のJavaDoc
    public void setPrivateKeyFile(String path){
        privateKeyFile = path;
    }
    // CipherCryptServiceMBean のJavaDoc
    public String getPrivateKeyFile(){
        return privateKeyFile;
    }
    
    /**
     * 鍵ペアを設定する。<p>
     *
     * @param k 鍵ペア
     */
    public void setKeyPair(KeyPair k){
        if(k == null){
            privateKey = null;
            publicKey = null;
        }else{
            setPrivateKey(k.getPrivate());
            setPublicKey(k.getPublic());
            isCreateKey = false;
            isLoadKey = false;
        }
    }
    
    /**
     * 鍵ペアを取得する。<p>
     *
     * @return 鍵ペア
     */
    public KeyPair getKeyPair(){
        return (publicKey != null || privateKey != null) ? new KeyPair(publicKey, privateKey) : null;
    }
    
    /**
     * 非公開鍵を取得する。<p>
     *
     * @return 非公開鍵
     */
    public PrivateKey getPrivateKey(){
        return privateKey;
    }
    
    /**
     * 非公開鍵を設定する。<p>
     *
     * @param key 非公開鍵
     */
    public void setPrivateKey(PrivateKey key){
        privateKey = key;
        if(key != null){
            keyPairAlgorithm = key.getAlgorithm();
            isCreateKey = false;
            isLoadKey = false;
        }
    }
    
    /**
     * 公開鍵を取得する。<p>
     *
     * @return 公開鍵
     */
    public PublicKey getPublicKey(){
        return publicKey;
    }
    
    /**
     * 公開鍵を取得する。<p>
     *
     * @param key 公開鍵
     */
    public void setPublicKey(PublicKey key){
        publicKey = key;
        if(key != null){
            keyPairAlgorithm = key.getAlgorithm();
            isCreateKey = false;
            isLoadKey = false;
        }
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
    
    /**
     * java.security.Signatureの初期化に使用するアルゴリズムパラメータを設定する。<p>
     * この属性を設定しない場合は、デフォルトのアルゴリズムパラメータが使用されます。<br>
     *
     * @param params アルゴリズムパラメータ
     */
    public void setSignatureAlgorithmParameterSpecsignatureAlgorithmParameterSpec(AlgorithmParameterSpec params){
        signatureAlgorithmParameterSpec = params;
    }
    
    /**
     * java.security.Signatureの初期化に使用するアルゴリズムパラメータを取得する。<p>
     *
     * @return アルゴリズムパラメータ
     */
    public AlgorithmParameterSpec getSignatureAlgorithmParameterSpec(){
        return signatureAlgorithmParameterSpec;
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
    
    // CipherCryptServiceMBean のJavaDoc
    public void setCheckOnStart(boolean isCheck){
        isCheckOnStart = isCheck;
    }
    // CipherCryptServiceMBean のJavaDoc
    public boolean isCheckOnStart(){
        return isCheckOnStart;
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
            storeFile = findFile(storePath, !isLoadKeyOnStart);
            File file = storeFile.getCanonicalFile();
            Object old = storeLockMap.putIfAbsent(file, file);
            storeLock = old == null ? file : old;
        }
        
        if(key == null && publicKey == null  && privateKey == null && storePath != null && isLoadKeyOnStart){
            if(storePassword == null){
                throw new IllegalArgumentException("StorePassword is null");
            }
            if(keyAlias == null && certificateAlias == null){
                throw new IllegalArgumentException("KeyAlias is null");
            }
            loadKey();
        }
        
        if(publicKey == null && privateKey == null && isCreatableKeyPair()){
            readPublicKeyBytes();
            readPrivateKeyBytes();
            KeyPair keyPair = createKeyPair();
            if(keyPair != null){
                privateKey = keyPair.getPrivate();
                publicKey = keyPair.getPublic();
                isCreateKey = true;
            }
        }else if(key == null && isCreatableSecretKey()){
            readSecretKeyBytes();
            key = createSecretKey();
            isCreateKey = true;
        }
        
        if(isCheckOnStart){
            if(key != null || (publicKey != null && privateKey != null)){
                if(transformation != null){
                    final byte[] encodeBytes = doEncodeInternal("test".getBytes(), iv);
                    final byte[] decodeBytes = doDecodeInternal(encodeBytes, iv);
                    if(!"test".equals(new String(decodeBytes))){
                        throw new IllegalArgumentException(
                            "This encryption cannot convert reversible. decode=" + new String(decodeBytes)
                        );
                    }
                }
                doMacInternal("test".getBytes());
            }
            doHashInternal("test".getBytes());
            if(signatureAlgorithm != null && privateKey != null && transformation != null){
                byte[] sign = doSignInternal("test".getBytes());
                if(publicKey != null){
                    doVerifyInternal("test".getBytes(), sign);
                }
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
        if((publicKey != null || privateKey != null) && isCreateKey){
            publicKey = null;
            privateKey = null;
            isCreateKey = false;
        }
        if(key != null && isCreateKey){
            key = null;
            isCreateKey = false;
        }
        algorithmParameters = null;
    }
    
    protected File findFile(String path, boolean isMakeDir){
        File result = new File(path);
        if(!result.exists() && getServiceNameObject() != null){
            ServiceMetaData metaData = ServiceManagerFactory.getServiceMetaData(getServiceNameObject());
            if(metaData != null){
                ServiceLoader loader = metaData.getServiceLoader();
                if(loader != null && loader.getServiceURL() != null){
                    String filePath = loader.getServiceURL().getFile();
                    if(filePath != null){
                        File dir = new File(filePath).getParentFile();
                        if(dir != null){
                            File newFile = new File(dir, path);
                            if(newFile.exists()){
                                result = newFile;
                            }
                        }
                    }
                }
            }
        }
        if(isMakeDir && result.exists() && result.getParentFile() != null && !result.getParentFile().exists()){
            result.getParentFile().mkdirs();
        }
        return result;
    }
    
    protected SecureRandom createSecureRandom() throws Exception{
        return SecureRandom.getInstance(rngAlgorithm);
    }
    
    protected boolean isCreatableKeyPair(){
        return keyPairAlgorithm != null;
    }
    
    protected boolean isCreatableSecretKey(){
        return secretKeyAlgorithm != null;
    }
    
    protected void readPublicKeyBytes() throws IOException{
        if(publicKeyFile != null){
            publicKeyBytes = readKeyBytes(publicKeyFile);
            if(publicKeyBytes == null){
                throw new FileNotFoundException("PublicKeyFile not found : " + publicKeyFile);
            }
        }
    }
    
    protected void readPrivateKeyBytes() throws IOException{
        if(privateKeyFile != null){
            privateKeyBytes = readKeyBytes(privateKeyFile);
            if(privateKeyBytes == null){
                throw new FileNotFoundException("PrivateKeyFile not found : " + privateKeyFile);
            }
        }
    }
    
    protected void readSecretKeyBytes() throws IOException{
        if(secretKeyFile != null){
            secretKeyBytes = readKeyBytes(secretKeyFile);
            if(secretKeyBytes == null){
                throw new FileNotFoundException("SecretKeyFile not found : " + secretKeyFile);
            }
        }
    }
    
    protected byte[] readKeyBytes(String path) throws IOException{
        File keyFile = findFile(path, false);
        if(keyFile.exists()){
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            InputStream is = new FileInputStream(keyFile);
            try{
                new StreamExchangeConverter().convert(is, baos);
            }finally{
                is.close();
            }
            return baos.toByteArray();
        }else{
            return null;
        }
    }
    
    /**
     * 秘密鍵を生成する。<p>
     *
     * @return 秘密鍵
     * @exception Exception キーの生成に失敗した場合
     */
    public SecretKey createSecretKey() throws Exception{
        if(secretKeyBytes != null){
            return createSecretKey(
                secretKeyAlgorithm,
                secretKeyBytes
            );
        }else if(pbePassword != null){
            return createSecretKey(
                secretKeyAlgorithm,
                keyFactoryProviderName,
                keyFactoryProvider,
                pbePassword,
                pbeSalt,
                pbeIterationCount,
                keySize
            );
        }else{
            return createSecretKey(
                secretKeyAlgorithm,
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
     * @param keyStr 鍵文字列
     * @return 秘密鍵
     * @exception Exception キーの生成に失敗した場合
     */
    public SecretKey createSecretKey(
        String keyAlgorithm,
        String keyStr
    ) throws Exception{
        return createSecretKey(keyAlgorithm, toBytes(keyStr));
    }
    
    /**
     * 秘密鍵を生成する。<p>
     *
     * @param keyAlgorithm 鍵アルゴリズム
     * @param filePath 鍵ファイルパス
     * @return 秘密鍵
     * @exception Exception キーの生成に失敗した場合
     */
    public SecretKey createSecretKeyFrom(
        String keyAlgorithm,
        String filePath
    ) throws Exception{
        InputStream is = new BufferedInputStream(new FileInputStream(findFile(filePath, false)));
        try{
            return createSecretKeyFrom(keyAlgorithm, is);
        }finally{
            is.close();
        }
    }
    
    /**
     * 秘密鍵を生成する。<p>
     *
     * @param keyAlgorithm 鍵アルゴリズム
     * @param is 鍵ストリーム
     * @return 秘密鍵
     * @exception Exception キーの生成に失敗した場合
     */
    public SecretKey createSecretKeyFrom(
        String keyAlgorithm,
        InputStream is
    ) throws Exception{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new StreamExchangeConverter().convert(is, baos);
        return createSecretKey(keyAlgorithm, baos.toByteArray());
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
     * 鍵ペアを生成する。<p>
     *
     * @return 鍵ペア
     * @exception Exception キーの生成に失敗した場合
     */
    public KeyPair createKeyPair() throws Exception{
        if(publicKeyBytes != null || privateKeyBytes != null){
            return createKeyPair(
                keyPairAlgorithm,
                keyGeneratorProviderName,
                keyGeneratorProvider,
                publicKeyBytes,
                privateKeyBytes
            );
        }else{
            return createKeyPair(
                keyPairAlgorithm,
                keyGeneratorProviderName,
                keyGeneratorProvider,
                keySize
            );
        }
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
     * 指定された公開鍵のバイト配列（X.509標準のASN.1エンコーディング）と、非公開鍵のバイト配列（PKCS#8標準のASN.1エンコーディング）で鍵ペアを生成する。<p>
     *
     * @param publicKeyBytes 公開鍵のバイト配列（X.509標準のASN.1エンコーディング）
     * @param privateKeyBytes 非公開鍵のバイト配列（PKCS#8標準のASN.1エンコーディング）
     * @return 鍵ペア
     * @exception Exception キーの生成に失敗した場合
     */
    public KeyPair createKeyPair(
        byte[] publicKeyBytes,
        byte[] privateKeyBytes
    ) throws Exception{
        return new KeyPair(
            publicKeyBytes == null ? null : createPublicKey(publicKeyBytes),
            privateKeyBytes == null ? null : createPrivateKey(privateKeyBytes)
        );
    }
    
    /**
     * 指定された公開鍵のバイト配列（X.509標準のASN.1エンコーディング）と、非公開鍵のバイト配列（PKCS#8標準のASN.1エンコーディング）で鍵ペアを生成する。<p>
     *
     * @param keyAlgorithm 鍵ペアアルゴリズム
     * @param keyFactoryProviderName 鍵生成プロバイダ名
     * @param keyFactoryProvider 鍵生成プロバイダ
     * @param publicKeyBytes 公開鍵のバイト配列（X.509標準のASN.1エンコーディング）
     * @param privateKeyBytes 非公開鍵のバイト配列（PKCS#8標準のASN.1エンコーディング）
     * @return 鍵ペア
     * @exception Exception キーの生成に失敗した場合
     */
    public KeyPair createKeyPair(
        String keyPairAlgorithm,
        String keyGeneratorProviderName,
        Provider keyGeneratorProvider,
        byte[] publicKeyBytes,
        byte[] privateKeyBytes
    ) throws Exception{
        return new KeyPair(
            publicKeyBytes == null ? null : createPublicKey(keyPairAlgorithm, keyGeneratorProviderName, keyGeneratorProvider, publicKeyBytes),
            privateKeyBytes == null ? null : createPrivateKey(keyPairAlgorithm, keyGeneratorProviderName, keyGeneratorProvider, privateKeyBytes)
        );
    }
    
    /**
     * 指定された公開鍵のバイト配列（X.509標準のASN.1エンコーディング）で公開鍵を生成する。<p>
     *
     * @param keyBytes 公開鍵のバイト配列（X.509標準のASN.1エンコーディング）
     * @return 公開鍵
     * @exception Exception キーの生成に失敗した場合
     */
    public PublicKey createPublicKey(byte[] keyBytes) throws Exception{
        return createPublicKey(keyPairAlgorithm, keyGeneratorProviderName, keyGeneratorProvider, keyBytes);
    }
    
    /**
     * 指定された公開鍵のファイル（X.509標準のASN.1エンコーディング）から公開鍵を生成する。<p>
     *
     * @param keyAlgorithm 鍵ペアアルゴリズム
     * @param keyFactoryProviderName 鍵生成プロバイダ名
     * @param keyFactoryProvider 鍵生成プロバイダ
     * @param filePath 公開鍵ファイルのパス（X.509標準のASN.1エンコーディング）
     * @return 公開鍵
     * @exception Exception キーの生成に失敗した場合
     */
    public PublicKey createPublicKeyFrom(
        String keyAlgorithm,
        String keyFactoryProviderName,
        Provider keyFactoryProvider,
        String filePath
    ) throws Exception{
        InputStream is = new BufferedInputStream(new FileInputStream(findFile(filePath, false)));
        try{
            return createPublicKeyFrom(keyAlgorithm, keyFactoryProviderName, keyFactoryProvider, is);
        }finally{
            is.close();
        }
    }
    
    /**
     * 指定された公開鍵のストリーム（X.509標準のASN.1エンコーディング）から公開鍵を生成する。<p>
     *
     * @param keyAlgorithm 鍵ペアアルゴリズム
     * @param keyFactoryProviderName 鍵生成プロバイダ名
     * @param keyFactoryProvider 鍵生成プロバイダ
     * @param is 公開鍵のストリーム（X.509標準のASN.1エンコーディング）
     * @return 公開鍵
     * @exception Exception キーの生成に失敗した場合
     */
    public PublicKey createPublicKeyFrom(
        String keyAlgorithm,
        String keyFactoryProviderName,
        Provider keyFactoryProvider,
        InputStream is
    ) throws Exception{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new StreamExchangeConverter().convert(is, baos);
        return createPublicKey(keyAlgorithm, keyFactoryProviderName, keyFactoryProvider, baos.toByteArray());
    }
    
    /**
     * 指定された公開鍵のバイト配列（X.509標準のASN.1エンコーディング）で公開鍵を生成する。<p>
     *
     * @param keyAlgorithm 鍵ペアアルゴリズム
     * @param keyFactoryProviderName 鍵生成プロバイダ名
     * @param keyFactoryProvider 鍵生成プロバイダ
     * @param keyBytes 公開鍵のバイト配列（X.509標準のASN.1エンコーディング）
     * @return 公開鍵
     * @exception Exception キーの生成に失敗した場合
     */
    public PublicKey createPublicKey(
        String keyAlgorithm,
        String keyFactoryProviderName,
        Provider keyFactoryProvider,
        byte[] keyBytes
    ) throws Exception{
        KeyFactory keyFac = null;
        if(keyFactoryProvider != null){
            keyFac = KeyFactory.getInstance(keyAlgorithm, keyFactoryProvider);
        }else if(keyFactoryProviderName != null){
            keyFac = KeyFactory.getInstance(keyAlgorithm, keyFactoryProviderName);
        }else{
            keyFac = KeyFactory.getInstance(keyAlgorithm);
        }
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        return keyFac.generatePublic(keySpec);
    }
    
    /**
     * 指定された非公開鍵のバイト配列（PKCS#8標準のASN.1エンコーディング）で非公開鍵を生成する。<p>
     *
     * @param keyBytes 非公開鍵のバイト配列（PKCS#8標準のASN.1エンコーディング）
     * @return 非公開鍵
     * @exception Exception キーの生成に失敗した場合
     */
    public PrivateKey createPrivateKey(byte[] keyBytes) throws Exception{
        return createPrivateKey(keyPairAlgorithm, keyGeneratorProviderName, keyGeneratorProvider, keyBytes);
    }
    
    /**
     * 指定された非公開鍵のファイル（PKCS#8標準のASN.1エンコーディング）から非公開鍵を生成する。<p>
     *
     * @param keyAlgorithm 鍵ペアアルゴリズム
     * @param keyFactoryProviderName 鍵生成プロバイダ名
     * @param keyFactoryProvider 鍵生成プロバイダ
     * @param filePath 非公開鍵ファイルのパス（PKCS#8標準のASN.1エンコーディング）
     * @return 非公開鍵
     * @exception Exception キーの生成に失敗した場合
     */
    public PrivateKey createPrivateKeyFrom(
        String keyAlgorithm,
        String keyFactoryProviderName,
        Provider keyFactoryProvider,
        String filePath
    ) throws Exception{
        InputStream is = new BufferedInputStream(new FileInputStream(findFile(filePath, false)));
        try{
            return createPrivateKeyFrom(keyAlgorithm, keyFactoryProviderName, keyFactoryProvider, is);
        }finally{
            is.close();
        }
    }
    
    /**
     * 指定された非公開鍵のストリーム（PKCS#8標準のASN.1エンコーディング）から非公開鍵を生成する。<p>
     *
     * @param keyAlgorithm 鍵ペアアルゴリズム
     * @param keyFactoryProviderName 鍵生成プロバイダ名
     * @param keyFactoryProvider 鍵生成プロバイダ
     * @param is 非公開鍵のストリーム（PKCS#8標準のASN.1エンコーディング）
     * @return 非公開鍵
     * @exception Exception キーの生成に失敗した場合
     */
    public PrivateKey createPrivateKeyFrom(
        String keyAlgorithm,
        String keyFactoryProviderName,
        Provider keyFactoryProvider,
        InputStream is
    ) throws Exception{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new StreamExchangeConverter().convert(is, baos);
        return createPrivateKey(keyAlgorithm, keyFactoryProviderName, keyFactoryProvider, baos.toByteArray());
    }
    
    /**
     * 指定された非公開鍵のバイト配列（PKCS#8標準のASN.1エンコーディング）で非公開鍵を生成する。<p>
     *
     * @param keyAlgorithm 鍵ペアアルゴリズム
     * @param keyFactoryProviderName 鍵生成プロバイダ名
     * @param keyFactoryProvider 鍵生成プロバイダ
     * @param keyBytes 非公開鍵のバイト配列（PKCS#8標準のASN.1エンコーディング）
     * @return 非公開鍵
     * @exception Exception キーの生成に失敗した場合
     */
    public PrivateKey createPrivateKey(
        String keyAlgorithm,
        String keyFactoryProviderName,
        Provider keyFactoryProvider,
        byte[] keyBytes
    ) throws Exception{
        KeyFactory keyFac = null;
        if(keyFactoryProvider != null){
            keyFac = KeyFactory.getInstance(keyAlgorithm, keyFactoryProvider);
        }else if(keyFactoryProviderName != null){
            keyFac = KeyFactory.getInstance(keyAlgorithm, keyFactoryProviderName);
        }else{
            keyFac = KeyFactory.getInstance(keyAlgorithm);
        }
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        return keyFac.generatePrivate(keySpec);
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
    
    /**
     * 鍵をキーストアから読み込む。<p>
     *
     * @exception Exception キーの読み込みに失敗した場合
     */
    public void loadKey() throws Exception{
        if(storePath != null){
            if(storePassword == null){
                throw new IllegalArgumentException("StorePassword is null");
            }
            if(keyAlias != null){
                if(keyPassword == null){
                    throw new IllegalArgumentException("KeyPassword is null");
                }
                Key k = loadKey(keyAlias, keyPassword);
                if(k != null){
                    if(k instanceof PrivateKey){
                        privateKey = (PrivateKey)k;
                    }else{
                        key = k;
                    }
                    isLoadKey = true;
                    isCreateKey = true;
                }
            }
            if(certificateAlias != null){
                Key k = loadKey(certificateAlias, null);
                if(k != null && k instanceof PublicKey){
                    publicKey = (PublicKey)k;
                    isLoadKey = true;
                    isCreateKey = true;
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
     * 指定された証明書を、キーストアから読み込む。<p>
     *
     * @param alias エイリアス
     * @return 証明書
     * @exception Exception 証明書の読み込みに失敗した場合
     */
    public java.security.cert.Certificate loadCertificate(String alias) throws Exception{
        KeyStore store = loadKeyStore();
        if(store.isCertificateEntry(alias)){
            return store.getCertificate(alias);
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
                is = new BufferedInputStream(new FileInputStream(storeFile));
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
        if(key != null){
            saveKey(key, keyAlias, keyPassword);
        }else if(privateKey != null){
            saveKey(privateKey, keyAlias, keyPassword);
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
     * 指定された証明書を、キーストアに書き込む。<p>
     *
     * @param cert 証明書
     * @param alias エイリアス
     * @exception Exception キーの書き込みに失敗した場合
     */
    public void saveCertificate(java.security.cert.Certificate cert, String alias) throws Exception{
        synchronized(storeLock){
            KeyStore store = loadKeyStore();
            store.setCertificateEntry(alias, cert);
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
            OutputStream os = new BufferedOutputStream(new FileOutputStream(storeFile));
            try{
                store.store(os, storePassword.toCharArray());
            }finally{
                os.close();
            }
        }
    }
    
    /**
     * 指定された鍵を指定されたファイルに書き出す。<p>
     *
     * @param key 鍵
     * @param filePath ファイルパス
     * @return フォーマット
     * @exception IOException 書き出しに失敗した場合
     */
    public String writeKey(Key key, String filePath) throws IOException{
        OutputStream os = new BufferedOutputStream(new FileOutputStream(findFile(filePath, true)));
        String format = null;
        try{
            format = writeKey(key, os);
            os.flush();
        }finally{
            os.close();
        }
        return format;
    }
    
    /**
     * 指定された鍵を指定されたストリームに書き出す。<p>
     *
     * @param key 鍵
     * @param os 出力ストリーム
     * @return フォーマット
     * @exception IOException 書き出しに失敗した場合
     */
    public String writeKey(Key key, OutputStream os) throws IOException{
        byte[] bytes = key.getEncoded();
        os.write(bytes);
        return key.getFormat();
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
            return toString(doEncodeBytes(str.getBytes(encoding)));
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
        try{
            if(iv == null && ivLength > 0){
                byte[] ivBytes = createSeed(ivLength);
                byte[] encodedBytes = doEncodeInternal(bytes, ivBytes);
                byte[] result = new byte[ivBytes.length + encodedBytes.length];
                System.arraycopy(ivBytes, 0, result, 0, ivBytes.length);
                System.arraycopy(encodedBytes, 0, result, ivBytes.length, encodedBytes.length);
                return result;
            }else{
                return doEncodeInternal(bytes, iv);
            }
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
        InputStream is = new BufferedInputStream(new FileInputStream(findFile(inFilePath, false)));
        OutputStream os = new BufferedOutputStream(new FileOutputStream(findFile(outFilePath, true)));
        try{
            doEncodeStream(is, os);
        }finally{
            is.close();
            os.close();
        }
    }
    
    public void doEncodeStream(InputStream is, OutputStream os) throws IOException{
        try{
            if(iv == null && ivLength > 0){
                byte[] ivBytes = createSeed(ivLength);
                os.write(ivBytes, 0 , ivBytes.length);
                doEncodeInternal(is, os, ivBytes);
            }else{
                doEncodeInternal(is, os, iv);
            }
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
        if(transformation == null){
            throw new UnsupportedOperationException(
                "Transformation is not specified."
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
            byte[] b = c.update(bytes, 0, length);
            if(b != null){
                os.write(b);
            }
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
        if(k == null && (publicKey != null || privateKey != null)){
            if(publicKey != null && privateKey == null){
                k = publicKey;
            }else if(publicKey == null && privateKey != null){
                k = privateKey;
            }else{
                switch(opmode){
                case Cipher.ENCRYPT_MODE:
                case Cipher.WRAP_MODE:
                    k = publicKey;
                    break;
                case Cipher.DECRYPT_MODE:
                    k = privateKey;
                    break;
                }
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
        if(k == null){
            throw new UnsupportedOperationException(
                "Key is not specified."
            );
        }
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
        return new String(doDecodeBytes(toBytes(str)), encoding);
    }
    public String doDecode(String str, String iv) throws Exception{
        return new String(doDecodeInternal(toBytes(str), iv == null ? null : toBytes(iv)), encoding);
    }
    
    public byte[] doDecodeBytes(byte[] bytes) throws Exception{
        if(iv == null && ivLength > 0){
            byte[] ivBytes = new byte[ivLength];
            System.arraycopy(bytes, 0, ivBytes, 0, ivLength);
            byte[] encodedBytes = new byte[bytes.length - ivLength];
            System.arraycopy(bytes, ivLength, encodedBytes, 0, encodedBytes.length);
            return doDecodeBytes(encodedBytes, ivBytes);
            
        }else{
            return doDecodeBytes(bytes, iv);
        }
    }
    
    public byte[] doDecodeBytes(byte[] bytes, byte[] iv) throws Exception{
        return doDecodeInternal(bytes, iv);
    }
    
    public void doDecodeFile(String inFilePath, String outFilePath) throws Exception{
        InputStream is = new BufferedInputStream(new FileInputStream(findFile(inFilePath, false)));
        OutputStream os = new BufferedOutputStream(new FileOutputStream(findFile(outFilePath, true)));
        try{
            doDecodeStream(is, os);
        }finally{
            is.close();
            os.close();
        }
    }
    
    public void doDecodeStream(InputStream is, OutputStream os) throws Exception{
        if(iv == null && ivLength > 0){
            byte[] ivBytes = new byte[ivLength];
            is.read(ivBytes, 0, ivLength);
            doDecodeInternal(is, os, ivBytes);
        }else{
            doDecodeInternal(is, os, iv);
        }
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
            byte[] b = c.update(bytes, 0, length);
            if(b != null){
                os.write(b);
            }
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
        InputStream is = new BufferedInputStream(new FileInputStream(findFile(filePath, false)));
        try{
            return doHashStream(is);
        }finally{
            is.close();
        }
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
        InputStream is = new BufferedInputStream(new FileInputStream(findFile(filePath, false)));
        try{
            return doMacStream(is);
        }finally{
            is.close();
        }
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
        Key k = selectKey(Cipher.ENCRYPT_MODE);
        if(k == null){
            throw new UnsupportedOperationException(
                "Key is not specified."
            );
        }
        if(macAlgorithmParameterSpec == null){
            mac.init(k);
        }else{
            mac.init(k, macAlgorithmParameterSpec);
        }
        final byte[] bytes = new byte[1024];
        int length = 0;
        while((length = is.read(bytes, 0, bytes.length)) != -1){
            mac.update(bytes, 0, length);
        }
        
        return mac.doFinal();
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
        }catch(InvalidAlgorithmParameterException e){
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
        }catch(InvalidAlgorithmParameterException e){
            // 起こらないはず
            getLogger().write(CC___00002, e);
        }
        return bytes;
    }
    
    // Crypt のJavaDoc
    public byte[] doSignFile(String filePath) throws IOException{
        InputStream is = new BufferedInputStream(new FileInputStream(findFile(filePath, false)));
        try{
            return doSignStream(is);
        }finally{
            is.close();
        }
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
        }catch(InvalidAlgorithmParameterException e){
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
     * @exception InvalidAlgorithmParameterException 指定されたアルゴリズムパラメータが無効または不適切な場合
     */
    protected byte[] doSignInternal(byte[] bytes)
     throws NoSuchProviderException, NoSuchAlgorithmException, InvalidKeyException, SignatureException, InvalidAlgorithmParameterException{
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
     * @exception InvalidAlgorithmParameterException 指定されたアルゴリズムパラメータが無効または不適切な場合
     */
    protected byte[] doSignInternal(InputStream is)
     throws NoSuchProviderException, NoSuchAlgorithmException, InvalidKeyException, SignatureException, IOException, InvalidAlgorithmParameterException{
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
        if(signatureAlgorithmParameterSpec != null){
            signature.setParameter(signatureAlgorithmParameterSpec);
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
        }catch(InvalidAlgorithmParameterException e){
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
        }catch(InvalidAlgorithmParameterException e){
            // 起こらないはず
            getLogger().write(CC___00002, e);
        }
        return false;
    }
    
    // Crypt のJavaDoc
    public boolean doVerifyFile(String filePath, byte[] sign) throws IOException{
        InputStream is = new BufferedInputStream(new FileInputStream(findFile(filePath, false)));
        try{
            return doVerifyStream(is, sign);
        }finally{
            is.close();
        }
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
        }catch(InvalidAlgorithmParameterException e){
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
     * @exception InvalidAlgorithmParameterException 指定されたアルゴリズムパラメータが無効または不適切な場合
     */
    protected boolean doVerifyInternal(byte[] bytes, byte[] sign)
     throws NoSuchProviderException, NoSuchAlgorithmException, InvalidKeyException, SignatureException, InvalidAlgorithmParameterException{
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
     throws NoSuchProviderException, NoSuchAlgorithmException, InvalidKeyException, SignatureException, IOException, InvalidAlgorithmParameterException{
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
        if(signatureAlgorithmParameterSpec != null){
            signature.setParameter(signatureAlgorithmParameterSpec);
        }
        signature.initVerify(publicKey);
        final byte[] bytes = new byte[1024];
        int length = 0;
        while((length = is.read(bytes, 0, bytes.length)) != -1){
            signature.update(bytes, 0, length);
        }
        return signature.verify(sign);
    }
    
    /**
     * 指定した鍵をラップする。<p>
     *
     * @param key ラップする鍵
     * @return ラップされた鍵バイト配列
     * @exception Exception 鍵のラップに失敗した場合
     */
    public byte[] doWrap(Key key) throws Exception{
        if(transformation == null){
            throw new UnsupportedOperationException(
                "Transformation is not specified."
            );
        }
        
        final Cipher c = createCipher();
        if(iv == null && ivLength > 0){
            byte[] ivBytes = createSeed(ivLength);
            intiCipher(c, Cipher.WRAP_MODE, ivBytes);
            
            byte[] wrappedBytes = c.wrap(key);
            byte[] result = new byte[ivLength + wrappedBytes.length];
            System.arraycopy(ivBytes, 0, result, 0, ivBytes.length);
            System.arraycopy(wrappedBytes, 0, result, ivBytes.length, wrappedBytes.length);
            return result;
        }else{
            intiCipher(c, Cipher.WRAP_MODE, iv);
            return c.wrap(key);
        }
    }
    
    /**
     * 指定した鍵のラップを解除する。<p>
     *
     * @param key ラップ解除する鍵
     * @param wrappedKeyType ラップされた鍵のタイプ
     * @return ラップ解除された鍵
     * @exception Exception 鍵のラップ解除に失敗した場合
     */
    public Key doUnwrap(byte[] wrappedKey, int wrappedKeyType) throws Exception{
        if(transformation == null){
            throw new UnsupportedOperationException(
                "Transformation is not specified."
            );
        }
        
        final Cipher c = createCipher();
        
        if(iv == null && ivLength > 0){
            byte[] ivBytes = new byte[ivLength];
            System.arraycopy(wrappedKey, 0, ivBytes, 0, ivLength);
            byte[] keyBytes = new byte[wrappedKey.length - ivLength];
            System.arraycopy(wrappedKey, ivLength, keyBytes, 0, keyBytes.length);
            intiCipher(c, Cipher.UNWRAP_MODE, ivBytes);
            return c.unwrap(keyBytes, selectKey(Cipher.WRAP_MODE).getAlgorithm(), wrappedKeyType);
        }else{
            intiCipher(c, Cipher.UNWRAP_MODE, iv);
            return c.unwrap(wrappedKey, selectKey(Cipher.WRAP_MODE).getAlgorithm(), wrappedKeyType);
        }
    }
    
    // ConverterのJavaDoc
    public Object convert(Object obj) throws ConvertException{
        if(obj == null){
            return null;
        }else if(obj instanceof byte[]){
            switch(convertType){
            case HASH_CONVERT:
                return doHashBytes((byte[])obj);
            case MAC_CONVERT:
                return doMacBytes((byte[])obj);
            case SIGN_CONVERT:
                return doSignBytes((byte[])obj);
            case DECODE_CONVERT:
                try{
                    return doDecodeBytes((byte[])obj);
                }catch(Exception e){
                    throw new ConvertException(e);
                }
            case ENCODE_CONVERT:
            default:
                return doEncodeBytes((byte[])obj);
            }
        }else if(obj instanceof InputStream){
            InputStream is = (InputStream)obj;
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            try{
                doDecodeStream(is, os);
            }catch(Exception e){
                throw new ConvertException(e);
            }
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
        case HASH_CONVERT:
            return doHash(str);
        case MAC_CONVERT:
            return doMac(str);
        case SIGN_CONVERT:
            return doSign(str);
        case DECODE_CONVERT:
            try{
                return doDecode(str);
            }catch(Exception e){
                throw new ConvertException(e);
            }
        case ENCODE_CONVERT:
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
            case HASH_CONVERT:
                os.write(doHashStream(is));
                os.flush();
                break;
            case MAC_CONVERT:
                os.write(doMacStream(is));
                os.flush();
                break;
            case SIGN_CONVERT:
                os.write(doSignStream(is));
                os.flush();
                break;
            case DECODE_CONVERT:
                doDecodeStream(is, os);
                break;
            case ENCODE_CONVERT:
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
            ServiceManagerFactory.DEFAULT_LOGGER.setSystemDebugEnabled(false);
            ServiceManagerFactory.DEFAULT_LOGGER.setDebugEnabled(false);
            ServiceManagerFactory.DEFAULT_LOGGER.setSystemInfoEnabled(false);
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
