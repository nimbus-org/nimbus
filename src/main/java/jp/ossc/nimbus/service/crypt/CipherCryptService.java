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
 * JCE(Java Cryptographic Extension)���g�p���āA�Í����@�\��񋟂���T�[�r�X�ł���B<p>
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
    
    // CipherCryptServiceMBean ��JavaDoc
    public void setTransformation(String trans){
        transformation = trans;
    }
    // CipherCryptServiceMBean ��JavaDoc
    public String getTransformation(){
        return transformation;
    }
    
    // CipherCryptServiceMBean ��JavaDoc
    public void setKey(Key k){
        key = k;
    }
    // CipherCryptServiceMBean ��JavaDoc
    public Key getKey(){
        return key;
    }
    
    // CipherCryptServiceMBean ��JavaDoc
    public void setCipherProvider(Provider p){
        cipherProvider = p;
    }
    // CipherCryptServiceMBean ��JavaDoc
    public Provider getCipherProvider(){
        return cipherProvider;
    }
    
    // CipherCryptServiceMBean ��JavaDoc
    public void setCipherProviderName(String name){
        cipherProviderName = name;
    }
    // CipherCryptServiceMBean ��JavaDoc
    public String getCipherProviderName(){
        return cipherProviderName;
    }
    
    // CipherCryptServiceMBean ��JavaDoc
    public void setAlgorithmParameters(AlgorithmParameters params){
        algorithmParameters = params;
    }
    // CipherCryptServiceMBean ��JavaDoc
    public AlgorithmParameters getAlgorithmParameters(){
        return algorithmParameters;
    }
    
    // CipherCryptServiceMBean ��JavaDoc
    public void setAlgorithmParameterSpec(AlgorithmParameterSpec params){
        algorithmParameterSpec = params;
    }
    // CipherCryptServiceMBean ��JavaDoc
    public AlgorithmParameterSpec getAlgorithmParameterSpec(){
        return algorithmParameterSpec;
    }
    
    // CipherCryptServiceMBean ��JavaDoc
    public void setSecureRandom(SecureRandom random){
        secureRandom = random;
    }
    // CipherCryptServiceMBean ��JavaDoc
    public SecureRandom getSecureRandom(){
        return secureRandom;
    }
    
    // CipherCryptServiceMBean ��JavaDoc
    public void setEncoding(String enc){
        encoding = enc;
    }
    // CipherCryptServiceMBean ��JavaDoc
    public String getEncoding(){
        return encoding;
    }
    
    // CipherCryptServiceMBean ��JavaDoc
    public void setHashAlgorithm(String algorithm){
        hashAlgorithm = algorithm;
    }
    // CipherCryptServiceMBean ��JavaDoc
    public String getHashAlgorithm(){
        return hashAlgorithm;
    }
    
    // CipherCryptServiceMBean ��JavaDoc
    public void setMessageDigestProvider(Provider p){
        messageDigestProvider = p;
    }
    // CipherCryptServiceMBean ��JavaDoc
    public Provider getMessageDigestProvider(){
        return messageDigestProvider;
    }
    
    // CipherCryptServiceMBean ��JavaDoc
    public void setMessageDigestProviderName(String name){
        messageDigestProviderName = name;
    }
    // CipherCryptServiceMBean ��JavaDoc
    public String getMessageDigestProviderName(){
        return messageDigestProviderName;
    }
    
    // CipherCryptServiceMBean ��JavaDoc
    public void setConvertType(int type){
        convertType = type;
    }
    // CipherCryptServiceMBean ��JavaDoc
    public int getConvertType(){
        return convertType;
    }
    
    // CipherCryptServiceMBean ��JavaDoc
    public void setStorePath(String path){
        storePath = path;
    }
    // CipherCryptServiceMBean ��JavaDoc
    public String getStorePath(){
        return storePath;
    }
    
    // CipherCryptServiceMBean ��JavaDoc
    public void setStoreType(String type){
        storeType = type;
    }
    // CipherCryptServiceMBean ��JavaDoc
    public String getStoreType(){
        return storeType;
    }
    
    // CipherCryptServiceMBean ��JavaDoc
    public void setStoreProviderName(String name){
        storeProviderName = name;
    }
    // CipherCryptServiceMBean ��JavaDoc
    public String getStoreProviderName(){
        return storeProviderName;
    }
    
    // CipherCryptServiceMBean ��JavaDoc
    public void setStoreProvider(Provider provider){
        storeProvider = provider;
    }
    // CipherCryptServiceMBean ��JavaDoc
    public Provider getStoreProvider(){
        return storeProvider;
    }
    
    // CipherCryptServiceMBean ��JavaDoc
    public void setStorePassword(String password){
        storePassword = password;
    }
    // CipherCryptServiceMBean ��JavaDoc
    public String getStorePassword(){
        return storePassword;
    }
    
    // CipherCryptServiceMBean ��JavaDoc
    public void setKeyAlias(String alias){
        keyAlias = alias;
    }
    // CipherCryptServiceMBean ��JavaDoc
    public String getKeyAlias(){
        return keyAlias;
    }
    
    // CipherCryptServiceMBean ��JavaDoc
    public void setKeyPassword(String password){
        keyPassword = password;
    }
    // CipherCryptServiceMBean ��JavaDoc
    public String getKeyPassword(){
        return keyPassword;
    }
    
    /**
     * �T�[�r�X�̊J�n�������s���B<p>
     *
     * @exception Exception �T�[�r�X�̊J�n�����Ɏ��s�����ꍇ
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
            final String encodeStr = doEncodeInternal("test");
            final String decodeStr = doDecodeInternal(encodeStr);
            if(!"test".equals(decodeStr)){
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
     * �L�[�X�g�A����閧����ǂݍ��ށB<p>
     *
     * @return �閧��
     * @exception Exception �L�[�̓ǂݍ��݂Ɏ��s�����ꍇ
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
    
    // Crypt ��JavaDoc
    public String doEncode(String str){
        try{
            return doEncodeInternal(str);
        }catch(NoSuchAlgorithmException e){
            // �N����Ȃ��͂�
            getLogger().write(CC___00001, e);
        }catch(NoSuchProviderException e){
            // �N����Ȃ��͂�
            getLogger().write(CC___00001, e);
        }catch(NoSuchPaddingException e){
            // �N����Ȃ��͂�
            getLogger().write(CC___00001, e);
        }catch(InvalidKeyException e){
            // �N����Ȃ��͂�
            getLogger().write(CC___00001, e);
        }catch(InvalidAlgorithmParameterException e){
            // �N����Ȃ��͂�
            getLogger().write(CC___00001, e);
        }catch(IllegalBlockSizeException e){
            // �N����Ȃ��͂�
            getLogger().write(CC___00001, e);
        }catch(BadPaddingException e){
            // �N����Ȃ��͂�
            getLogger().write(CC___00001, e);
        }catch(UnsupportedEncodingException e){
            // �N����Ȃ��͂�
            getLogger().write(CC___00001, e);
        }
        return str;
    }
    
    /**
     * �Í�������B<p>
     *
     * @param str �Í����Ώۂ̕�����
     * @return �Í�����̕�����
     * @exception NoSuchAlgorithmException �w�肳�ꂽ�A���S���Y�������݂��Ȃ��ꍇ
     * @exception NoSuchProviderException �w�肳�ꂽ�v���o�C�_�����݂��Ȃ��ꍇ
     * @exception NoSuchPaddingException �w�肳�ꂽ�p�f�B���O�@�\�����݂��Ȃ��ꍇ
     * @exception InvalidKeyException �w�肳�ꂽ���������ȕ������A�����̌��A���������Ȃǂ̖����Ȍ��ł���ꍇ
     * @exception InvalidAlgorithmParameterException �w�肳�ꂽ�A���S���Y���p�����[�^�������܂��͕s�K�؂ȏꍇ
     * @exception IllegalBlockSizeException �u���b�N�Í��ɒ񋟂��ꂽ�f�[�^�̒������������Ȃ��ꍇ
     * @exception BadPaddingException ����̃p�f�B���O�@�\�����̓f�[�^�ɑ΂��ė\������Ă���̂Ƀf�[�^���K�؂Ƀp�f�B���O����Ȃ��ꍇ
     * @exception UnsupportedEncodingException �w�肳�ꂽ�����G���R�[�f�B���O���T�|�[�g����Ă��Ȃ��ꍇ
     */
    protected String doEncodeInternal(String str)
     throws NoSuchAlgorithmException, NoSuchProviderException,
            NoSuchPaddingException, InvalidKeyException,
            InvalidAlgorithmParameterException,
            IllegalBlockSizeException, BadPaddingException,
            UnsupportedEncodingException{
        if(transformation == null || key == null){
            throw new UnsupportedOperationException(
                "Transformation or key is not specified."
            );
        }
        
        if(str == null){
            return null;
        }
        
        final Cipher c = createCipher();
        
        intiCipher(c, Cipher.ENCRYPT_MODE);
        
        return toHexString(c.doFinal(str.getBytes(encoding)));
    }
    
    /**
     * javax.crypto.Cipher�𐶐�����B<p>
     *
     * @return javax.crypto.Cipher
     * @exception NoSuchAlgorithmException �w�肳�ꂽ�A���S���Y�������݂��Ȃ��ꍇ
     * @exception NoSuchProviderException �w�肳�ꂽ�v���o�C�_�����݂��Ȃ��ꍇ
     * @exception NoSuchPaddingException �w�肳�ꂽ�p�f�B���O�@�\�����݂��Ȃ��ꍇ
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
     * javax.crypto.Cipher������������B<p>
     *
     * @param c javax.crypto.Cipher
     * @param opmode ���̈Í��̑��샂�[�h 
     * @exception InvalidKeyException �w�肳�ꂽ���������ȕ������A�����̌��A���������Ȃǂ̖����Ȍ��ł���ꍇ
     * @exception InvalidAlgorithmParameterException �w�肳�ꂽ�A���S���Y���p�����[�^�������܂��͕s�K�؂ȏꍇ
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
     * �w�肳�ꂽ�o�C�g�z���16�i���̕�����ɕϊ�����B<p>
     *
     * @param bytes �o�C�g�z��
     * @return 16�i��������
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
    
    // Crypt ��JavaDoc
    public String doDecode(String str){
        try{
            return doDecodeInternal(str);
        }catch(NoSuchAlgorithmException e){
            // �N����Ȃ��͂�
            getLogger().write(CC___00002, e);
        }catch(NoSuchProviderException e){
            // �N����Ȃ��͂�
            getLogger().write(CC___00002, e);
        }catch(NoSuchPaddingException e){
            // �N����Ȃ��͂�
            getLogger().write(CC___00002, e);
        }catch(InvalidKeyException e){
            // �N����Ȃ��͂�
            getLogger().write(CC___00002, e);
        }catch(InvalidAlgorithmParameterException e){
            // �N����Ȃ��͂�
            getLogger().write(CC___00002, e);
        }catch(IllegalBlockSizeException e){
            // �N����Ȃ��͂�
            getLogger().write(CC___00002, e);
        }catch(BadPaddingException e){
            // �N����Ȃ��͂�
            getLogger().write(CC___00002, e);
        }catch(UnsupportedEncodingException e){
            // �N����Ȃ��͂�
            getLogger().write(CC___00002, e);
        }
        return str;
    }
    
    /**
     * ����������B<p>
     *
     * @param str �������Ώۂ̕�����
     * @return ��������̕�����
     * @exception NoSuchAlgorithmException �w�肳�ꂽ�A���S���Y�������݂��Ȃ��ꍇ
     * @exception NoSuchProviderException �w�肳�ꂽ�v���o�C�_�����݂��Ȃ��ꍇ
     * @exception NoSuchPaddingException �w�肳�ꂽ�p�f�B���O�@�\�����݂��Ȃ��ꍇ
     * @exception InvalidKeyException �w�肳�ꂽ���������ȕ������A�����̌��A���������Ȃǂ̖����Ȍ��ł���ꍇ
     * @exception InvalidAlgorithmParameterException �w�肳�ꂽ�A���S���Y���p�����[�^�������܂��͕s�K�؂ȏꍇ
     * @exception IllegalBlockSizeException �u���b�N�Í��ɒ񋟂��ꂽ�f�[�^�̒������������Ȃ��ꍇ
     * @exception BadPaddingException ����̃p�f�B���O�@�\�����̓f�[�^�ɑ΂��ė\������Ă���̂Ƀf�[�^���K�؂Ƀp�f�B���O����Ȃ��ꍇ
     * @exception UnsupportedEncodingException �w�肳�ꂽ�����G���R�[�f�B���O���T�|�[�g����Ă��Ȃ��ꍇ
     */
    protected String doDecodeInternal(String str)
     throws NoSuchAlgorithmException, NoSuchProviderException,
            NoSuchPaddingException, InvalidKeyException,
            InvalidAlgorithmParameterException,
            IllegalBlockSizeException, BadPaddingException,
            UnsupportedEncodingException{
        if(str == null){
            return null;
        }
        
        final Cipher c = createCipher();
        
        intiCipher(c, Cipher.DECRYPT_MODE);
        
        return new String(c.doFinal(toBytes(str)), encoding);
    }
    
    /**
     * �w�肳�ꂽ16�i���̕�������o�C�g�z��ɕϊ�����B<p>
     *
     * @param hex 16�i��������
     * @return �o�C�g�z��
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
    
    // Crypt ��JavaDoc
    public String doHash(String str){
        try{
            return doHashInternal(str);
        }catch(NoSuchProviderException e){
            // �N����Ȃ��͂�
            getLogger().write(CC___00003, e);
        }catch(NoSuchAlgorithmException e){
            // �N����Ȃ��͂�
            getLogger().write(CC___00003, e);
        }catch(UnsupportedEncodingException e){
            // �N����Ȃ��͂�
            getLogger().write(CC___00003, e);
        }
        return str;
    }
    
    /**
     * �n�b�V������B<p>
     *
     * @param str �n�b�V���Ώۂ̕�����
     * @return �n�b�V����̕�����
     * @exception NoSuchAlgorithmException �w�肳�ꂽ�A���S���Y�������݂��Ȃ��ꍇ
     * @exception NoSuchProviderException �w�肳�ꂽ�v���o�C�_�����݂��Ȃ��ꍇ
     * @exception UnsupportedEncodingException �w�肳�ꂽ�����G���R�[�f�B���O���T�|�[�g����Ă��Ȃ��ꍇ
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
    
    // Converter��JavaDoc
    public Object convert(Object obj) throws ConvertException{
        if(obj == null){
            return null;
        }else{
            return convert(
                (String)(obj instanceof String ? obj : String.valueOf(obj))
            );
        }
    }
    
    // StringConverter��JavaDoc
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
