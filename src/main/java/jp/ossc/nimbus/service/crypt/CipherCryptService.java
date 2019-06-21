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
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.security.*;
import java.security.spec.*;
import javax.crypto.*;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.util.converter.*;

/**
 * JCE(Java Cryptographic Extension)を使用して、暗号化機能を提供するサービスである。<p>
 *
 * @author M.Takata
 */
public class  CipherCryptService extends ServiceBase
 implements Crypt, StringConverter, ReversibleConverter, CipherCryptServiceMBean{
    
    private static final long serialVersionUID = 5230161454391953789L;
    
    private static final String CC___00001 = "CC___00001";
    private static final String CC___00002 = "CC___00002";
    private static final String CC___00003 = "CC___00003";
    
    protected String transformation = DEFAULT_TRANSFORMATION;
    protected Key key;
    protected Provider cipherProvider;
    protected String cipherProviderName;
    protected Provider messageDigestProvider;
    protected String messageDigestProviderName;
    protected AlgorithmParameters algorithmParameters;
    protected AlgorithmParameterSpec algorithmParameterSpec;
    protected SecureRandom secureRandom;
    protected String encoding = DEFAULT_ENCODING;
    protected String hashAlgorithm = DEFAULT_HASH_ALGORITHM;
    protected int convertType;
    
    protected String storePath;
    protected String storeType;
    protected String storeProviderName;
    protected Provider storeProvider;
    protected String storePassword;
    protected String keyAlias;
    protected String keyPassword;
    
    // CipherCryptServiceMBean のJavaDoc
    public void setTransformation(String trans){
        transformation = trans;
    }
    // CipherCryptServiceMBean のJavaDoc
    public String getTransformation(){
        return transformation;
    }
    
    // CipherCryptServiceMBean のJavaDoc
    public void setKey(Key k){
        key = k;
    }
    // CipherCryptServiceMBean のJavaDoc
    public Key getKey(){
        return key;
    }
    
    // CipherCryptServiceMBean のJavaDoc
    public void setCipherProvider(Provider p){
        cipherProvider = p;
    }
    // CipherCryptServiceMBean のJavaDoc
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
    
    // CipherCryptServiceMBean のJavaDoc
    public void setAlgorithmParameters(AlgorithmParameters params){
        algorithmParameters = params;
    }
    // CipherCryptServiceMBean のJavaDoc
    public AlgorithmParameters getAlgorithmParameters(){
        return algorithmParameters;
    }
    
    // CipherCryptServiceMBean のJavaDoc
    public void setAlgorithmParameterSpec(AlgorithmParameterSpec params){
        algorithmParameterSpec = params;
    }
    // CipherCryptServiceMBean のJavaDoc
    public AlgorithmParameterSpec getAlgorithmParameterSpec(){
        return algorithmParameterSpec;
    }
    
    // CipherCryptServiceMBean のJavaDoc
    public void setSecureRandom(SecureRandom random){
        secureRandom = random;
    }
    // CipherCryptServiceMBean のJavaDoc
    public SecureRandom getSecureRandom(){
        return secureRandom;
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
    
    // CipherCryptServiceMBean のJavaDoc
    public void setMessageDigestProvider(Provider p){
        messageDigestProvider = p;
    }
    // CipherCryptServiceMBean のJavaDoc
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
    public void setConvertType(int type){
        convertType = type;
    }
    // CipherCryptServiceMBean のJavaDoc
    public int getConvertType(){
        return convertType;
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
    
    // CipherCryptServiceMBean のJavaDoc
    public void setStoreProvider(Provider provider){
        storeProvider = provider;
    }
    // CipherCryptServiceMBean のJavaDoc
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
        if(key == null && storePath != null){
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
        }
        if(key != null){
            final byte[] encodeBytes = doEncodeInternal("test".getBytes());
            final byte[] decodeBytes = doDecodeInternal(encodeBytes);
            if(!"test".equals(new String(decodeBytes))){
                throw new IllegalArgumentException(
                    "This encryption cannot convert reversible."
                );
            }
        }
        if(hashAlgorithm != null){
            doHashInternal("test");
        }
        if(key == null && hashAlgorithm == null){
            throw new IllegalArgumentException(
                "It is necessary to specify either of key or hashAlgorithm."
            );
        }
    }
    
    /**
     * キーストアから秘密鍵を読み込む。<p>
     *
     * @return 秘密鍵
     * @exception Exception キーの読み込みに失敗した場合
     */
    protected Key loadKey() throws Exception{
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
        store.load(is, storePassword.toCharArray());
        return store.getKey(keyAlias, keyPassword.toCharArray());
    }
    
    // Crypt のJavaDoc
    public String doEncode(String str){
        try{
            return toHexString(doEncodeInternal(str.getBytes(encoding)));
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
            return doEncodeInternal(bytes);
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
    
    /**
     * 暗号化する。<p>
     *
     * @param bytes 暗号化対象のバイト配列
     * @return 暗号化後のバイト配列
     * @exception NoSuchAlgorithmException 指定されたアルゴリズムが存在しない場合
     * @exception NoSuchProviderException 指定されたプロバイダが存在しない場合
     * @exception NoSuchPaddingException 指定されたパディング機構が存在しない場合
     * @exception InvalidKeyException 指定された鍵が無効な符号化、長さの誤り、未初期化などの無効な鍵である場合
     * @exception InvalidAlgorithmParameterException 指定されたアルゴリズムパラメータが無効または不適切な場合
     * @exception IllegalBlockSizeException ブロック暗号に提供されたデータの長さが正しくない場合
     * @exception BadPaddingException 特定のパディング機構が入力データに対して予期されているのにデータが適切にパディングされない場合
     */
    protected byte[] doEncodeInternal(byte[] bytes)
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
        
        intiCipher(c, Cipher.ENCRYPT_MODE);
        
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
     * @exception InvalidKeyException 指定された鍵が無効な符号化、長さの誤り、未初期化などの無効な鍵である場合
     * @exception InvalidAlgorithmParameterException 指定されたアルゴリズムパラメータが無効または不適切な場合
     */
    protected void intiCipher(Cipher c, int opmode)
     throws InvalidKeyException, InvalidAlgorithmParameterException{
        if(algorithmParameters != null){
            if(secureRandom == null){
                c.init(opmode, key, algorithmParameters);
            }else{
                c.init(
                    opmode,
                    key,
                    algorithmParameters,
                    secureRandom
                );
            }
        }else if(algorithmParameterSpec != null){
            if(secureRandom == null){
                c.init(opmode, key, algorithmParameterSpec);
            }else{
                c.init(
                    opmode,
                    key,
                    algorithmParameterSpec,
                    secureRandom
                );
            }
        }else if(secureRandom != null){
            c.init(opmode, key, secureRandom);
        }else{
            c.init(opmode, key);
        }
    }
    
    /**
     * 指定されたバイト配列を16進数の文字列に変換する。<p>
     *
     * @param bytes バイト配列
     * @return 16進数文字列
     */
    protected static String toHexString(byte[] bytes){
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
    
    // Crypt のJavaDoc
    public String doDecode(String str){
        try{
            return new String(doDecodeInternal(toBytes(str)), encoding);
        }catch(NoSuchAlgorithmException e){
            // 起こらないはず
            getLogger().write(CC___00002, e);
        }catch(NoSuchProviderException e){
            // 起こらないはず
            getLogger().write(CC___00002, e);
        }catch(NoSuchPaddingException e){
            // 起こらないはず
            getLogger().write(CC___00002, e);
        }catch(InvalidKeyException e){
            // 起こらないはず
            getLogger().write(CC___00002, e);
        }catch(InvalidAlgorithmParameterException e){
            // 起こらないはず
            getLogger().write(CC___00002, e);
        }catch(IllegalBlockSizeException e){
            // 起こらないはず
            getLogger().write(CC___00002, e);
        }catch(BadPaddingException e){
            // 起こらないはず
            getLogger().write(CC___00002, e);
        }catch(UnsupportedEncodingException e){
            // 起こらないはず
            getLogger().write(CC___00002, e);
        }
        return str;
    }
    
    public byte[] doDecodeBytes(byte[] bytes){
        try{
            return doDecodeInternal(bytes);
        }catch(NoSuchAlgorithmException e){
            // 起こらないはず
            getLogger().write(CC___00002, e);
        }catch(NoSuchProviderException e){
            // 起こらないはず
            getLogger().write(CC___00002, e);
        }catch(NoSuchPaddingException e){
            // 起こらないはず
            getLogger().write(CC___00002, e);
        }catch(InvalidKeyException e){
            // 起こらないはず
            getLogger().write(CC___00002, e);
        }catch(InvalidAlgorithmParameterException e){
            // 起こらないはず
            getLogger().write(CC___00002, e);
        }catch(IllegalBlockSizeException e){
            // 起こらないはず
            getLogger().write(CC___00002, e);
        }catch(BadPaddingException e){
            // 起こらないはず
            getLogger().write(CC___00002, e);
        }
        return bytes;
    }
    
    /**
     * 復号化する。<p>
     *
     * @param bytes 復号化対象のバイト配列
     * @return 復号化後のバイト配列
     * @exception NoSuchAlgorithmException 指定されたアルゴリズムが存在しない場合
     * @exception NoSuchProviderException 指定されたプロバイダが存在しない場合
     * @exception NoSuchPaddingException 指定されたパディング機構が存在しない場合
     * @exception InvalidKeyException 指定された鍵が無効な符号化、長さの誤り、未初期化などの無効な鍵である場合
     * @exception InvalidAlgorithmParameterException 指定されたアルゴリズムパラメータが無効または不適切な場合
     * @exception IllegalBlockSizeException ブロック暗号に提供されたデータの長さが正しくない場合
     * @exception BadPaddingException 特定のパディング機構が入力データに対して予期されているのにデータが適切にパディングされない場合
     */
    protected byte[] doDecodeInternal(byte[] bytes)
     throws NoSuchAlgorithmException, NoSuchProviderException,
            NoSuchPaddingException, InvalidKeyException,
            InvalidAlgorithmParameterException,
            IllegalBlockSizeException, BadPaddingException{
        if(bytes == null){
            return null;
        }
        
        final Cipher c = createCipher();
        
        intiCipher(c, Cipher.DECRYPT_MODE);
        
        return c.doFinal(bytes);
    }
    
    /**
     * 指定された16進数の文字列をバイト配列に変換する。<p>
     *
     * @param hex 16進数文字列
     * @return バイト配列
     */
    protected static byte[] toBytes(String hex){
        final byte[] bytes = new byte[hex.length() / 2];
        for(int i = 0, max = hex.length(); i < max; i+=2){
            bytes[i / 2] = (byte)(Integer.parseInt(
                hex.substring(i, i + 2), 16) & 0x000000FF
            );
        }
        return bytes;
    }
    
    // Crypt のJavaDoc
    public String doHash(String str){
        try{
            return doHashInternal(str);
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
    
    /**
     * ハッシュする。<p>
     *
     * @param str ハッシュ対象の文字列
     * @return ハッシュ後の文字列
     * @exception NoSuchAlgorithmException 指定されたアルゴリズムが存在しない場合
     * @exception NoSuchProviderException 指定されたプロバイダが存在しない場合
     * @exception UnsupportedEncodingException 指定された文字エンコーディングがサポートされていない場合
     */
    protected String doHashInternal(String str)
     throws NoSuchProviderException, NoSuchAlgorithmException,
            UnsupportedEncodingException{
        if(hashAlgorithm == null){
            throw new UnsupportedOperationException(
                "HashAlgorithm is not specified."
            );
        }
        if(str == null){
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
        
        return toHexString(
            messageDigest.digest(
                str.getBytes(encoding)
            )
        );
    }
    
    // ConverterのJavaDoc
    public Object convert(Object obj) throws ConvertException{
        if(obj == null){
            return null;
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
            return doDecode(str);
        case POSITIVE_CONVERT:
        default:
            return doEncode(str);
        }
    }
    
}
