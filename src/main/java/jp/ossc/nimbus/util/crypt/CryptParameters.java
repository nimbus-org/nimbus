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
 * �Í����p�����[�^�B<p>
 * JCE(Java Cryptographic Extension)���g�p���āA�p�����[�^���Í������郆�[�e�B���e�B�N���X�ł���B<br>
 * <p>
 * ���������ɂ́A�Í���������̉�₂�h�����߂Ƀn�b�V���l�ɂ���₃`�F�b�N���s���@�\���񋟂���B<br>
 * �܂��A�Í����p�����[�^�������s���ɓ��肵�čė��p���鎖�ɂ��u�Ȃ肷�܂��v��h�����߂ɁA�Í����p�����[�^������̗L�������`�F�b�N���s���@�\���񋟂���B<br>
 * <p>
 * �ȉ��ɁA�g�p���@�̃T���v���R�[�h�������B<br>
 * <pre>
 *     // �閧��
 *     final byte[] KEY = "12345678".getBytes();
 *     
 *     // �n�b�V�����ʌ�(�C��)
 *     final String HASH_KEY = "hogehoge";
 *     
 *     // CryptParameters�̐���
 *     CryptParameters cipher = new CryptParameters(KEY, HASH_KEY);
 *     
 *     // �Í����p�����[�^�̐���
 *     final Map params = cipher.createParametersMap();
 *     params.put("user_id", "m-takata");
 *     params.put("access_id", "hoge");
 *     params.put("password", "fugafuga");
 *     System.out.println("params : " + params);
 *     
 *     // ��ₖh�~�p�n�b�V���̐���
 *     final String hash = cipher.createHash(params);
 *     System.out.println("hash : " + hash);
 *     
 *     // �Í����p�����x�N�^�̐���
 *     final String iv = cipher.createInitialVector();
 *     System.out.println("iv : " + iv);
 *     
 *     // �Í���
 *     final String encrypt = cipher.encrypt(iv, params);
 *     System.out.println("encrypt : " + encrypt);
 *     
 *     // �������i��₃`�F�b�N�y�їL�������`�F�b�N�t���j
 *     final Map decrypt = cipher.decrypt(iv, encrypt, hash, 10000);
 *     System.out.println("decrypt : " + decrypt);
 * </pre>
 * ���s���ʂ̗���ȉ��Ɏ����B<br>
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
     * �L�������`�F�b�N�Ɏg�p������t������̃f�t�H���g�t�H�[�}�b�g�B<p>
     */
    public static final String DEFAULT_DATE_FORMAT_PATTERN = "yyyyMMddHHmmssSSSz";
    
    /**
     * �Í���������y�уn�b�V��������̃f�t�H���g�����G���R�[�f�B���O�B<p>
     */
    public static final String DEFAULT_ENCODING = "ISO_8859-1";
    
    /**
     * �f�t�H���g�̕ϊ������i�A���S���Y��/���[�h/�p�f�B���O�j�B<p>
     */
    public static final String DEFAULT_TRANSFORMATION = "DES/CBC/PKCS5Padding";
    
    /**
     * �f�t�H���g�̔閧���A���S���Y���B
     */
    public static final String DEFAULT_SECRET_KEY_ALGORITHM = "DES";
    
    /**
     * �f�t�H���g�̏����x�N�^���B
     */
    public static final int DEFAULT_INITIAL_VECTOR_LENGTH = 8;
    
    /**
     * �f�t�H���g�̃n�b�V���A���S���Y���B
     */
    public static final String DEFAULT_HASH_ALGORITHM = "MD5";
    
    /**
     * �L�������`�F�b�N�p�����[�^�̃f�t�H���g�̃p�����[�^���B<p>
     */
    public static final String DEFAULT_DATE_KEY = "$D";
    
    /**
     * �L�������`�F�b�N�p�����[�^�̋��p�����[�^���B<p>
     * ���̋��p�����[�^�ŁA���삳�������ꍇ�́A{@link #setDateKey(String)}�Ŏw�肷�邩�A�V�X�e���v���p�e�B{@link #SYSTEM_PROPERTY_OLD_DATE_KEY}�ŁAtrue���w�肵�ĉ������B<br>
     *
     * @deprecated {@link #DEFAULT_DATE_KEY}�ɒu���������܂���
     */
    public static final String DATE_KEY
        = CryptParameters.class.getName().replaceAll("\\.", "/") + "/DATE";
    
    /**
     * �L�������`�F�b�N�p�����[�^�̃p�����[�^���̃f�t�H���g�l���A{@link #DATE_KEY}�ɕς��邽�߂̃V�X�e���v���p�e�B���B<p>
     * -Djp.ossc.nimbus.util.crypt.CryptParameters.oldDateKey=true�Ǝw�肷��B<br>
     */
    public static final String SYSTEM_PROPERTY_OLD_DATE_KEY = CryptParameters.class.getName() + ".oldDateKey";
    
    /**
     * �P��p�����[�^�̃p�����[�^���B<p>
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
     * �C���X�^���X�𐶐�����B<p>
     *
     * @param key �閧���̃o�C�g�z��
     * @exception InvalidKeyException �w�肳�ꂽ�������̈Í��̏������ɕs�K�؂ȏꍇ�A�܂��͎w�肳�ꂽ���̃T�C�Y���ő勖�e���T�C�Y (�ݒ肳��Ă���Ǌ��|���V�[�t�@�C���ɂ�茈��) �𒴂���ꍇ
     */
    public CryptParameters(byte[] key) throws InvalidKeyException{
        this(key, null);
    }
    
    /**
     * �C���X�^���X�𐶐�����B<p>
     *
     * @param storePath �L�[�X�g�A�̃p�X
     * @param storeType �L�[�X�g�A�̎��
     * @param storeProviderName �L�[�X�g�A�̃v���o�C�_��
     * @param storePassword �L�[�X�g�A�̃p�X���[�h
     * @param alias �閧���̕ʖ�
     * @param password �閧���̃p�X���[�h
     * @exception IOException �L�[�X�g�A�f�[�^�ɓ��o�͂܂��͌`���̖�肪�������ꍇ
     * @exception KeyStoreException �v���o�C�_�ɁA�v�����ꂽ�L�[�X�g�A�^���Ȃ��ꍇ
     * @exception CertificateException �L�[�X�g�A�̂ǂ̏ؖ��������[�h�ł��Ȃ������ꍇ
     * @exception UnrecoverableKeyException �w�肳�ꂽ�p�X���[�h���Ԉ���Ă���ꍇ�ȂǁA���𕜌��ł��Ȃ��ꍇ
     * @exception NoSuchProviderException �w�肳�ꂽ�v���o�C�_���T�|�[�g����Ă��Ȃ��ꍇ
     * @exception NoSuchAlgorithmException �L�[�X�g�A�̊��S������������A���S���Y����������Ȃ������ꍇ
     * @exception InvalidKeyException �w�肳�ꂽ�������̈Í��̏������ɕs�K�؂ȏꍇ�A�܂��͎w�肳�ꂽ���̃T�C�Y���ő勖�e���T�C�Y (�ݒ肳��Ă���Ǌ��|���V�[�t�@�C���ɂ�茈��) �𒴂���ꍇ
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
     * �C���X�^���X�𐶐�����B<p>
     *
     * @param storePath �L�[�X�g�A�̃p�X
     * @param storeType �L�[�X�g�A�̎��
     * @param storeProvider �L�[�X�g�A�̃v���o�C�_
     * @param storePassword �L�[�X�g�A�̃p�X���[�h
     * @param alias �閧���̕ʖ�
     * @param password �閧���̃p�X���[�h
     * @exception IOException �L�[�X�g�A�f�[�^�ɓ��o�͂܂��͌`���̖�肪�������ꍇ
     * @exception KeyStoreException �v���o�C�_�ɁA�v�����ꂽ�L�[�X�g�A�^���Ȃ��ꍇ
     * @exception CertificateException �L�[�X�g�A�̂ǂ̏ؖ��������[�h�ł��Ȃ������ꍇ
     * @exception UnrecoverableKeyException �w�肳�ꂽ�p�X���[�h���Ԉ���Ă���ꍇ�ȂǁA���𕜌��ł��Ȃ��ꍇ
     * @exception NoSuchProviderException �w�肳�ꂽ�v���o�C�_���T�|�[�g����Ă��Ȃ��ꍇ
     * @exception NoSuchAlgorithmException �L�[�X�g�A�̊��S������������A���S���Y����������Ȃ������ꍇ
     * @exception InvalidKeyException �w�肳�ꂽ�������̈Í��̏������ɕs�K�؂ȏꍇ�A�܂��͎w�肳�ꂽ���̃T�C�Y���ő勖�e���T�C�Y (�ݒ肳��Ă���Ǌ��|���V�[�t�@�C���ɂ�茈��) �𒴂���ꍇ
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
     * �C���X�^���X�𐶐�����B<p>
     *
     * @param key �閧��
     * @exception InvalidKeyException �w�肳�ꂽ�������̈Í��̏������ɕs�K�؂ȏꍇ�A�܂��͎w�肳�ꂽ���̃T�C�Y���ő勖�e���T�C�Y (�ݒ肳��Ă���Ǌ��|���V�[�t�@�C���ɂ�茈��) �𒴂���ꍇ
     */
    public CryptParameters(Key key) throws InvalidKeyException{
        this(key, null);
    }
    
    /**
     * �n�b�V�������p�̃C���X�^���X�𐶐�����B<p>
     *
     * @param hashKey �n�b�V�����ʌ�
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
            // �N����Ȃ��͂�
            throw new UnexpectedCryptException(e);
        }catch(NoSuchAlgorithmException e){
            // �N����Ȃ��͂�
            throw new UnexpectedCryptException(e);
        }catch(NoSuchPaddingException e){
            // �N����Ȃ��͂�
            throw new UnexpectedCryptException(e);
        }catch(InvalidAlgorithmParameterException e){
            // �N����Ȃ��͂�
            throw new UnexpectedCryptException(e);
        }catch(IllegalBlockSizeException e){
            // �N����Ȃ��͂�
            throw new UnexpectedCryptException(e);
        }catch(InvalidKeyException e){
            // �N����Ȃ��͂�
            throw new UnexpectedCryptException(e);
        }
    }
    
    /**
     * �C���X�^���X�𐶐�����B<p>
     *
     * @param key �閧���̃o�C�g�z��
     * @param hashKey �n�b�V�����ʌ�
     * @exception InvalidKeyException �w�肳�ꂽ�������̈Í��̏������ɕs�K�؂ȏꍇ�A�܂��͎w�肳�ꂽ���̃T�C�Y���ő勖�e���T�C�Y (�ݒ肳��Ă���Ǌ��|���V�[�t�@�C���ɂ�茈��) �𒴂���ꍇ
     */
    public CryptParameters(byte[] key, String hashKey) throws InvalidKeyException{
        this(key == null ? null : new SecretKeySpec(key, DEFAULT_SECRET_KEY_ALGORITHM), hashKey);
        
    }
    
    /**
     * �C���X�^���X�𐶐�����B<p>
     *
     * @param storePath �L�[�X�g�A�̃p�X
     * @param storeType �L�[�X�g�A�̎��
     * @param storeProviderName �L�[�X�g�A�̃v���o�C�_��
     * @param storePassword �L�[�X�g�A�̃p�X���[�h
     * @param alias �閧���̕ʖ�
     * @param password �閧���̃p�X���[�h
     * @param hashKey �n�b�V�����ʌ�
     * @exception IOException �L�[�X�g�A�f�[�^�ɓ��o�͂܂��͌`���̖�肪�������ꍇ
     * @exception KeyStoreException �v���o�C�_�ɁA�v�����ꂽ�L�[�X�g�A�^���Ȃ��ꍇ
     * @exception CertificateException �L�[�X�g�A�̂ǂ̏ؖ��������[�h�ł��Ȃ������ꍇ
     * @exception UnrecoverableKeyException �w�肳�ꂽ�p�X���[�h���Ԉ���Ă���ꍇ�ȂǁA���𕜌��ł��Ȃ��ꍇ
     * @exception NoSuchProviderException �w�肳�ꂽ�v���o�C�_���T�|�[�g����Ă��Ȃ��ꍇ
     * @exception NoSuchAlgorithmException �L�[�X�g�A�̊��S������������A���S���Y����������Ȃ������ꍇ
     * @exception InvalidKeyException �w�肳�ꂽ�������̈Í��̏������ɕs�K�؂ȏꍇ�A�܂��͎w�肳�ꂽ���̃T�C�Y���ő勖�e���T�C�Y (�ݒ肳��Ă���Ǌ��|���V�[�t�@�C���ɂ�茈��) �𒴂���ꍇ
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
            // �N����Ȃ��͂�
            throw new UnexpectedCryptException(e);
        }catch(NoSuchAlgorithmException e){
            // �N����Ȃ��͂�
            throw new UnexpectedCryptException(e);
        }catch(NoSuchPaddingException e){
            // �N����Ȃ��͂�
            throw new UnexpectedCryptException(e);
        }catch(InvalidAlgorithmParameterException e){
            // �N����Ȃ��͂�
            throw new UnexpectedCryptException(e);
        }catch(IllegalBlockSizeException e){
            // �N����Ȃ��͂�
            throw new UnexpectedCryptException(e);
        }
    }
    
    /**
     * �C���X�^���X�𐶐�����B<p>
     *
     * @param storePath �L�[�X�g�A�̃p�X
     * @param storeType �L�[�X�g�A�̎��
     * @param storeProvider �L�[�X�g�A�̃v���o�C�_
     * @param storePassword �L�[�X�g�A�̃p�X���[�h
     * @param alias �閧���̕ʖ�
     * @param password �閧���̃p�X���[�h
     * @param hashKey �n�b�V�����ʌ�
     * @exception IOException �L�[�X�g�A�f�[�^�ɓ��o�͂܂��͌`���̖�肪�������ꍇ
     * @exception KeyStoreException �v���o�C�_�ɁA�v�����ꂽ�L�[�X�g�A�^���Ȃ��ꍇ
     * @exception CertificateException �L�[�X�g�A�̂ǂ̏ؖ��������[�h�ł��Ȃ������ꍇ
     * @exception UnrecoverableKeyException �w�肳�ꂽ�p�X���[�h���Ԉ���Ă���ꍇ�ȂǁA���𕜌��ł��Ȃ��ꍇ
     * @exception NoSuchProviderException �w�肳�ꂽ�v���o�C�_���T�|�[�g����Ă��Ȃ��ꍇ
     * @exception NoSuchAlgorithmException �L�[�X�g�A�̊��S������������A���S���Y����������Ȃ������ꍇ
     * @exception InvalidKeyException �w�肳�ꂽ�������̈Í��̏������ɕs�K�؂ȏꍇ�A�܂��͎w�肳�ꂽ���̃T�C�Y���ő勖�e���T�C�Y (�ݒ肳��Ă���Ǌ��|���V�[�t�@�C���ɂ�茈��) �𒴂���ꍇ
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
            // �N����Ȃ��͂�
            throw new UnexpectedCryptException(e);
        }catch(NoSuchAlgorithmException e){
            // �N����Ȃ��͂�
            throw new UnexpectedCryptException(e);
        }catch(NoSuchPaddingException e){
            // �N����Ȃ��͂�
            throw new UnexpectedCryptException(e);
        }catch(InvalidAlgorithmParameterException e){
            // �N����Ȃ��͂�
            throw new UnexpectedCryptException(e);
        }catch(IllegalBlockSizeException e){
            // �N����Ȃ��͂�
            throw new UnexpectedCryptException(e);
        }
    }
    
    /**
     * �C���X�^���X�𐶐�����B<p>
     *
     * @param key �閧��
     * @param hashKey �n�b�V�����ʌ�
     * @exception InvalidKeyException �w�肳�ꂽ�������̈Í��̏������ɕs�K�؂ȏꍇ�A�܂��͎w�肳�ꂽ���̃T�C�Y���ő勖�e���T�C�Y (�ݒ肳��Ă���Ǌ��|���V�[�t�@�C���ɂ�茈��) �𒴂���ꍇ
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
            // �N����Ȃ��͂�
            throw new UnexpectedCryptException(e);
        }catch(NoSuchAlgorithmException e){
            // �N����Ȃ��͂�
            throw new UnexpectedCryptException(e);
        }catch(NoSuchPaddingException e){
            // �N����Ȃ��͂�
            throw new UnexpectedCryptException(e);
        }catch(InvalidAlgorithmParameterException e){
            // �N����Ȃ��͂�
            throw new UnexpectedCryptException(e);
        }catch(IllegalBlockSizeException e){
            // �N����Ȃ��͂�
            throw new UnexpectedCryptException(e);
        }
    }
    
    /**
     * �C���X�^���X�𐶐�����B<p>
     *
     * @param key �閧���̃o�C�g�z��
     * @param algorithm �閧���̃A���S���Y��
     * @param transformation �ϊ������i�A���S���Y��/���[�h/�p�f�B���O�j
     * @param ivLength �����x�N�^��
     * @param provider �v���o�C�_��
     * @param hashKey �n�b�V�����ʌ�
     * @exception NoSuchProviderException �w�肳�ꂽ�v���o�C�_���T�|�[�g����Ă��Ȃ��ꍇ
     * @exception NoSuchAlgorithmException �w�肳��A���S���Y�����T�|�[�g����Ă��Ȃ��ꍇ
     * @exception InvalidKeyException �w�肳�ꂽ�������̈Í��̏������ɕs�K�؂ȏꍇ�A�܂��͎w�肳�ꂽ���̃T�C�Y���ő勖�e���T�C�Y (�ݒ肳��Ă���Ǌ��|���V�[�t�@�C���ɂ�茈��) �𒴂���ꍇ
     * @exception InvalidAlgorithmParameterException �w�肳�ꂽ�A���S���Y���p�����[�^���L���Ȑ��� (�ݒ肳��Ă���Ǌ��|���V�[�t�@�C���ɂ�茈��) �𒴂���Í������x�������ꍇ
     * @exception IllegalBlockSizeException ���̈Í����u���b�N�Í��ł���A�p�f�B���O���v������Ă��炸�A���̈Í��ŏ������ꂽ�f�[�^�̓��͒��̍��v���u���b�N�T�C�Y�̔{���łȂ��ꍇ
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
     * �C���X�^���X�𐶐�����B<p>
     *
     * @param key �閧���̃o�C�g�z��
     * @param algorithm �閧���̃A���S���Y��
     * @param transformation �ϊ������i�A���S���Y��/���[�h/�p�f�B���O�j
     * @param ivLength �����x�N�^��
     * @param provider �v���o�C�_
     * @param hashKey �n�b�V�����ʌ�
     * @exception NoSuchProviderException �w�肳�ꂽ�v���o�C�_���T�|�[�g����Ă��Ȃ��ꍇ
     * @exception NoSuchAlgorithmException �w�肳��A���S���Y�����T�|�[�g����Ă��Ȃ��ꍇ
     * @exception InvalidKeyException �w�肳�ꂽ�������̈Í��̏������ɕs�K�؂ȏꍇ�A�܂��͎w�肳�ꂽ���̃T�C�Y���ő勖�e���T�C�Y (�ݒ肳��Ă���Ǌ��|���V�[�t�@�C���ɂ�茈��) �𒴂���ꍇ
     * @exception InvalidAlgorithmParameterException �w�肳�ꂽ�A���S���Y���p�����[�^���L���Ȑ��� (�ݒ肳��Ă���Ǌ��|���V�[�t�@�C���ɂ�茈��) �𒴂���Í������x�������ꍇ
     * @exception IllegalBlockSizeException ���̈Í����u���b�N�Í��ł���A�p�f�B���O���v������Ă��炸�A���̈Í��ŏ������ꂽ�f�[�^�̓��͒��̍��v���u���b�N�T�C�Y�̔{���łȂ��ꍇ
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
     * �C���X�^���X�𐶐�����B<p>
     *
     * @param storePath �L�[�X�g�A�̃p�X
     * @param storeType �L�[�X�g�A�̎��
     * @param storeProviderName �L�[�X�g�A�̃v���o�C�_��
     * @param storePassword �L�[�X�g�A�̃p�X���[�h
     * @param alias �閧���̕ʖ�
     * @param password �閧���̃p�X���[�h
     * @param transformation �ϊ������i�A���S���Y��/���[�h/�p�f�B���O�j
     * @param ivLength �����x�N�^��
     * @param provider �v���o�C�_��
     * @param hashKey �n�b�V�����ʌ�
     * @exception IOException �L�[�X�g�A�f�[�^�ɓ��o�͂܂��͌`���̖�肪�������ꍇ
     * @exception KeyStoreException �v���o�C�_�ɁA�v�����ꂽ�L�[�X�g�A�^���Ȃ��ꍇ
     * @exception CertificateException �L�[�X�g�A�̂ǂ̏ؖ��������[�h�ł��Ȃ������ꍇ
     * @exception UnrecoverableKeyException �w�肳�ꂽ�p�X���[�h���Ԉ���Ă���ꍇ�ȂǁA���𕜌��ł��Ȃ��ꍇ
     * @exception NoSuchProviderException �w�肳�ꂽ�v���o�C�_���T�|�[�g����Ă��Ȃ��ꍇ
     * @exception NoSuchAlgorithmException �L�[�X�g�A�̊��S������������A���S���Y����������Ȃ������ꍇ�A�܂��͎w�肳�ꂽ�A���S���Y�����T�|�[�g����Ă��Ȃ��ꍇ
     * @exception InvalidKeyException �w�肳�ꂽ�������̈Í��̏������ɕs�K�؂ȏꍇ�A�܂��͎w�肳�ꂽ���̃T�C�Y���ő勖�e���T�C�Y (�ݒ肳��Ă���Ǌ��|���V�[�t�@�C���ɂ�茈��) �𒴂���ꍇ
     * @exception InvalidAlgorithmParameterException �w�肳�ꂽ�A���S���Y���p�����[�^���L���Ȑ��� (�ݒ肳��Ă���Ǌ��|���V�[�t�@�C���ɂ�茈��) �𒴂���Í������x�������ꍇ
     * @exception IllegalBlockSizeException ���̈Í����u���b�N�Í��ł���A�p�f�B���O���v������Ă��炸�A���̈Í��ŏ������ꂽ�f�[�^�̓��͒��̍��v���u���b�N�T�C�Y�̔{���łȂ��ꍇ
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
     * �C���X�^���X�𐶐�����B<p>
     *
     * @param storePath �L�[�X�g�A�̃p�X
     * @param storeType �L�[�X�g�A�̎��
     * @param storeProvider �L�[�X�g�A�̃v���o�C�_
     * @param storePassword �L�[�X�g�A�̃p�X���[�h
     * @param alias �閧���̕ʖ�
     * @param password �閧���̃p�X���[�h
     * @param transformation �ϊ������i�A���S���Y��/���[�h/�p�f�B���O�j
     * @param ivLength �����x�N�^��
     * @param provider �v���o�C�_
     * @param hashKey �n�b�V�����ʌ�
     * @exception IOException �L�[�X�g�A�f�[�^�ɓ��o�͂܂��͌`���̖�肪�������ꍇ
     * @exception KeyStoreException �v���o�C�_�ɁA�v�����ꂽ�L�[�X�g�A�^���Ȃ��ꍇ
     * @exception CertificateException �L�[�X�g�A�̂ǂ̏ؖ��������[�h�ł��Ȃ������ꍇ
     * @exception UnrecoverableKeyException �w�肳�ꂽ�p�X���[�h���Ԉ���Ă���ꍇ�ȂǁA���𕜌��ł��Ȃ��ꍇ
     * @exception NoSuchProviderException �w�肳�ꂽ�v���o�C�_���T�|�[�g����Ă��Ȃ��ꍇ
     * @exception NoSuchAlgorithmException �L�[�X�g�A�̊��S������������A���S���Y����������Ȃ������ꍇ�A�܂��͎w�肳�ꂽ�A���S���Y�����T�|�[�g����Ă��Ȃ��ꍇ
     * @exception InvalidKeyException �w�肳�ꂽ�������̈Í��̏������ɕs�K�؂ȏꍇ�A�܂��͎w�肳�ꂽ���̃T�C�Y���ő勖�e���T�C�Y (�ݒ肳��Ă���Ǌ��|���V�[�t�@�C���ɂ�茈��) �𒴂���ꍇ
     * @exception InvalidAlgorithmParameterException �w�肳�ꂽ�A���S���Y���p�����[�^���L���Ȑ��� (�ݒ肳��Ă���Ǌ��|���V�[�t�@�C���ɂ�茈��) �𒴂���Í������x�������ꍇ
     * @exception IllegalBlockSizeException ���̈Í����u���b�N�Í��ł���A�p�f�B���O���v������Ă��炸�A���̈Í��ŏ������ꂽ�f�[�^�̓��͒��̍��v���u���b�N�T�C�Y�̔{���łȂ��ꍇ
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
     * �C���X�^���X�𐶐�����B<p>
     *
     * @param key �閧��
     * @param transformation �ϊ������i�A���S���Y��/���[�h/�p�f�B���O�j
     * @param ivLength �����x�N�^��
     * @param provider �v���o�C�_��
     * @param hashKey �n�b�V�����ʌ�
     * @exception NoSuchProviderException �w�肳�ꂽ�v���o�C�_���T�|�[�g����Ă��Ȃ��ꍇ
     * @exception NoSuchAlgorithmException �w�肳�ꂽ�A���S���Y�����T�|�[�g����Ă��Ȃ��ꍇ
     * @exception InvalidKeyException �w�肳�ꂽ�������̈Í��̏������ɕs�K�؂ȏꍇ�A�܂��͎w�肳�ꂽ���̃T�C�Y���ő勖�e���T�C�Y (�ݒ肳��Ă���Ǌ��|���V�[�t�@�C���ɂ�茈��) �𒴂���ꍇ
     * @exception InvalidAlgorithmParameterException �w�肳�ꂽ�A���S���Y���p�����[�^���L���Ȑ��� (�ݒ肳��Ă���Ǌ��|���V�[�t�@�C���ɂ�茈��) �𒴂���Í������x�������ꍇ
     * @exception IllegalBlockSizeException ���̈Í����u���b�N�Í��ł���A�p�f�B���O���v������Ă��炸�A���̈Í��ŏ������ꂽ�f�[�^�̓��͒��̍��v���u���b�N�T�C�Y�̔{���łȂ��ꍇ
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
     * �C���X�^���X�𐶐�����B<p>
     *
     * @param key �閧��
     * @param transformation �ϊ������i�A���S���Y��/���[�h/�p�f�B���O�j
     * @param ivLength �����x�N�^��
     * @param provider �v���o�C�_
     * @param hashKey �n�b�V�����ʌ�
     * @exception NoSuchProviderException �w�肳�ꂽ�v���o�C�_���T�|�[�g����Ă��Ȃ��ꍇ
     * @exception NoSuchAlgorithmException �w�肳�ꂽ�A���S���Y�����T�|�[�g����Ă��Ȃ��ꍇ
     * @exception InvalidKeyException �w�肳�ꂽ�������̈Í��̏������ɕs�K�؂ȏꍇ�A�܂��͎w�肳�ꂽ���̃T�C�Y���ő勖�e���T�C�Y (�ݒ肳��Ă���Ǌ��|���V�[�t�@�C���ɂ�茈��) �𒴂���ꍇ
     * @exception InvalidAlgorithmParameterException �w�肳�ꂽ�A���S���Y���p�����[�^���L���Ȑ��� (�ݒ肳��Ă���Ǌ��|���V�[�t�@�C���ɂ�茈��) �𒴂���Í������x�������ꍇ
     * @exception IllegalBlockSizeException ���̈Í����u���b�N�Í��ł���A�p�f�B���O���v������Ă��炸�A���̈Í��ŏ������ꂽ�f�[�^�̓��͒��̍��v���u���b�N�T�C�Y�̔{���łȂ��ꍇ
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
     * �L�[�X�g�A����閧����ǂݍ��ށB<p>
     *
     * @param storePath �L�[�X�g�A�̃p�X
     * @param storeType �L�[�X�g�A�̎��
     * @param storeProviderName �L�[�X�g�A�̃v���o�C�_��
     * @param storeProvider �L�[�X�g�A�̃v���o�C�_
     * @param storePassword �L�[�X�g�A�̃p�X���[�h
     * @param alias �閧���̕ʖ�
     * @param password �閧���̃p�X���[�h
     * @exception IOException �L�[�X�g�A�f�[�^�ɓ��o�͂܂��͌`���̖�肪�������ꍇ
     * @exception NoSuchProviderException �w�肳�ꂽ�v���o�C�_���T�|�[�g����Ă��Ȃ��ꍇ
     * @exception KeyStoreException �v���o�C�_�ɁA�v�����ꂽ�L�[�X�g�A�^���Ȃ��ꍇ
     * @exception NoSuchAlgorithmException �L�[�X�g�A�̊��S������������A���S���Y����������Ȃ������ꍇ
     * @exception CertificateException �L�[�X�g�A�̂ǂ̏ؖ��������[�h�ł��Ȃ������ꍇ
     * @exception UnrecoverableKeyException �w�肳�ꂽ�p�X���[�h���Ԉ���Ă���ꍇ�ȂǁA���𕜌��ł��Ȃ��ꍇ
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
     * �C���X�^���X������������B<p>
     *
     * @param key �閧��
     * @param transformation �ϊ������i�A���S���Y��/���[�h/�p�f�B���O�j
     * @param ivLength �����x�N�^��
     * @param providerName �v���o�C�_��
     * @param provider �v���o�C�_
     * @param hashKey �n�b�V�����ʌ�
     * @exception NoSuchProviderException �w�肳�ꂽ�v���o�C�_���T�|�[�g����Ă��Ȃ��ꍇ
     * @exception NoSuchAlgorithmException �w�肳�ꂽ�A���S���Y�����T�|�[�g����Ă��Ȃ��ꍇ
     * @exception InvalidKeyException �w�肳�ꂽ�������̈Í��̏������ɕs�K�؂ȏꍇ�A�܂��͎w�肳�ꂽ���̃T�C�Y���ő勖�e���T�C�Y (�ݒ肳��Ă���Ǌ��|���V�[�t�@�C���ɂ�茈��) �𒴂���ꍇ
     * @exception InvalidAlgorithmParameterException �w�肳�ꂽ�A���S���Y���p�����[�^���L���Ȑ��� (�ݒ肳��Ă���Ǌ��|���V�[�t�@�C���ɂ�茈��) �𒴂���Í������x�������ꍇ
     * @exception IllegalBlockSizeException ���̈Í����u���b�N�Í��ł���A�p�f�B���O���v������Ă��炸�A���̈Í��ŏ������ꂽ�f�[�^�̓��͒��̍��v���u���b�N�T�C�Y�̔{���łȂ��ꍇ
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
                    // �Í����ł͋N����Ȃ��͂�
                    throw new UnexpectedCryptException(e);
                }catch(UnsupportedEncodingException e){
                    // �N����Ȃ��͂�
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
                    // �N����Ȃ��͂�
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
                    // �Í����ł͋N����Ȃ��͂�
                    throw new UnexpectedCryptException(e);
                }catch(UnsupportedEncodingException e){
                    // �N����Ȃ��͂�
                    throw new UnexpectedCryptException(e);
                }
                c.init(
                    Cipher.DECRYPT_MODE,
                    secretKey
                );
                try{
                    c.doFinal(encrypt);
                }catch(BadPaddingException e){
                    // �N����Ȃ��͂�
                    throw new UnexpectedCryptException(e);
                }
            }
        }
        setHashKey(hashKey);
    }
    
    /**
     * �L�������`�F�b�N�p�����[�^�̃p�����[�^����ݒ肷��B<p>
     * �f�t�H���g�́A{@link #DEFAULT_DATE_KEY}�B<br>
     *
     * @param key �p�����[�^��
     */
    public void setDateKey(String key){
        dateKey = key;
    }
    
    /**
     * �L�������`�F�b�N�p�����[�^�̃p�����[�^�����擾����B<p>
     *
     * @return �p�����[�^��
     */
    public String getDateKey(){
        return dateKey;
    }
    
    /**
     * �L�������`�F�b�N�Ɏg�p������t������̃t�H�[�}�b�g��ݒ肷��B<p>
     * �f�t�H���g�́A{@link #DEFAULT_DATE_FORMAT_PATTERN}�B<br>
     *
     * @param format ���t�t�H�[�}�b�g
     * @exception IllegalArgumentException �w�肳�ꂽ���t�t�H�[�}�b�g���������Ȃ��ꍇ
     */
    public void setDateFormat(String format) throws IllegalArgumentException{
        new SimpleDateFormat(format);
        dateFormat = format;
    }
    
    /**
     * �L�������`�F�b�N�Ɏg�p������t������̃t�H�[�}�b�g���擾����B<p>
     *
     * @return ���t�t�H�[�}�b�g
     */
    public String getDateFormat(){
        return dateFormat;
    }
    
    /**
     * �Í���������y�уn�b�V��������̕����G���R�[�f�B���O��ݒ肷��B<p>
     * �f�t�H���g�́A{@link #DEFAULT_ENCODING}�B<br>
     *
     * @param encoding �����G���R�[�f�B���O
     * @exception UnsupportedEncodingException �w�肳�ꂽ�����G���R�[�f�B���O���T�|�[�g����Ă��Ȃ��ꍇ
     */
    public void setEncoding(String encoding)
     throws UnsupportedEncodingException{
        "".getBytes(encoding);
        this.encoding = encoding;
    }
    
    /**
     * �Í���������y�уn�b�V��������̕����G���R�[�f�B���O���擾����B<p>
     *
     * @return �����G���R�[�f�B���O
     */
    public String getEncoding(){
        return encoding;
    }
    
    /**
     * �Í���/�������̕ϊ��������擾����B<p>
     *
     * @return �ϊ�����
     */
    public String getTransformation(){
        return transformation;
    }
    
    /**
     * �閧���A���S���Y����ݒ肷��B<p>
     * �f�t�H���g�́A{@link #DEFAULT_SECRET_KEY_ALGORITHM}�B<br>
     *
     * @param algorithm �A���S���Y��
     */
    public void setAlgorithm(String algorithm){
        this.algorithm = algorithm;
        if(algorithm != null && secretKey != null && secretKey instanceof SecretKeySpec){
            secretKey = new SecretKeySpec(((SecretKeySpec)secretKey).getEncoded(), algorithm);
        }
    }
    
    /**
     * �閧���A���S���Y�����擾����B<p>
     *
     * @return �A���S���Y��
     */
    public String getAlgorithm(){
        return algorithm;
    }
    
    /**
     * �閧����ݒ肷��B<p>
     * 
     * @param key �閧���̃o�C�g�z��
     */
    public void setKey(byte[] key){
        if(key == null){
            secretKey = null;
        }else{
            secretKey = new SecretKeySpec(key, algorithm);
        }
    }
    
    /**
     * �閧����ݒ肷��B<p>
     * 
     * @param key �閧��
     */
    public void setKey(Key key){
        if(key == null){
            secretKey = null;
        }else{
            secretKey = key;
        }
    }
    
    /**
     * �閧�����擾����B<p>
     * 
     * @return �閧��
     */
    public Key getKey(){
        return secretKey;
    }
    
    /**
     * �n�b�V���A���S���Y����ݒ肷��B<p>
     * �f�t�H���g�́A{@link #DEFAULT_HASH_ALGORITHM}�B<br>
     *
     * @param algorithm �A���S���Y��
     * @exception NoSuchAlgorithmException �w�肳�ꂽ�A���S���Y�����T�|�[�g����Ă��Ȃ��ꍇ
     */
    public void setHashAlgorithm(String algorithm)
     throws NoSuchAlgorithmException{
        MessageDigest.getInstance(algorithm);
        hashAlgorithm = algorithm;
        hashProviderName = null;
        hashProvider = null;
    }
    
    /**
     * �n�b�V���A���S���Y����ݒ肷��B<p>
     * �f�t�H���g�́A{@link #DEFAULT_HASH_ALGORITHM}�B<br>
     *
     * @param algorithm �A���S���Y��
     * @param provider �v���o�C�_��
     * @exception NoSuchAlgorithmException �w�肳�ꂽ�A���S���Y�����T�|�[�g����Ă��Ȃ��ꍇ
     * @exception NoSuchProviderException �w�肳�ꂽ�v���o�C�_���T�|�[�g����Ă��Ȃ��ꍇ
     */
    public void setHashAlgorithm(String algorithm, String provider)
     throws NoSuchAlgorithmException, NoSuchProviderException{
        MessageDigest.getInstance(algorithm, provider);
        hashAlgorithm = algorithm;
        hashProviderName = provider;
        hashProvider = null;
    }
    
    /**
     * �n�b�V���A���S���Y����ݒ肷��B<p>
     * �f�t�H���g�́A{@link #DEFAULT_HASH_ALGORITHM}�B<br>
     *
     * @param algorithm �A���S���Y��
     * @param provider �v���o�C�_
     * @exception NoSuchAlgorithmException �w�肳�ꂽ�A���S���Y�����T�|�[�g����Ă��Ȃ��ꍇ
     * @exception NoSuchProviderException �w�肳�ꂽ�v���o�C�_���T�|�[�g����Ă��Ȃ��ꍇ
     */
    public void setHashAlgorithm(String algorithm, Provider provider)
     throws NoSuchAlgorithmException, NoSuchProviderException{
        MessageDigest.getInstance(algorithm, provider);
        hashAlgorithm = algorithm;
        hashProviderName = null;
        hashProvider = provider;
    }
    
    /**
     * �n�b�V���A���S���Y�����擾����B<p>
     *
     * @return �A���S���Y��
     */
    public String getHashAlgorithm(){
        return hashAlgorithm;
    }
    
    /**
     * �n�b�V�����ʌ���ݒ肷��B<p>
     *
     * @param key �n�b�V�����ʌ�
     */
    public void setHashKey(String key){
        hashKey = key;
    }
    
    /**
     * �n�b�V�����ʌ����擾����B<p>
     *
     * @return �n�b�V�����ʌ�
     */
    public String getHashKey(){
        return hashKey;
    }
    
    /**
     * �Í�������p�����[�^���i�[����}�b�v�𐶐�����B<p>
     * �Í�������p�����[�^���i�[����}�b�v�́A�K���������̃��\�b�h�Ő��������}�b�v���g�p����K�v�͂Ȃ��B�A���A���������ɁA�Í����p�����[�^������̗L�������̃`�F�b�N��L���ɂ������ꍇ�́A���̃��\�b�h�Ő��������}�b�v���g�p����K�v������B<br>
     * ���̃}�b�v�́A�ė��p���Ă͂Ȃ�Ȃ��B�܂��A����������Ȃ��B<br>
     * �}�b�v���ė��p�������ꍇ�́A{@link #createParametersMap(Map)}���g�p���邱�ƁB<br>
     *
     * @return �Í�������p�����[�^���i�[����}�b�v
     * @see #createParametersMap(Map)
     */
    public Map createParametersMap(){
        final Map params = new LinkedHashMap();
        createParametersMap(params);
        return params;
    }
    
    /**
     * �Í�������p�����[�^���i�[����}�b�v�𐶐�����B<p>
     * �Í�������p�����[�^���i�[����}�b�v�́A�K���������̃��\�b�h�Ő��������}�b�v���g�p����K�v�͂Ȃ��B�A���A���������ɁA�Í����p�����[�^������̗L�������̃`�F�b�N��L���ɂ������ꍇ�́A���̃��\�b�h�Ő��������}�b�v���g�p����K�v������B<br>
     * �ė��p�̂��߂ɓn���ꂽ�}�b�vparams�́A�����������B<br>
     *
     * @param params �ė��p�̂��߂̃}�b�v
     * @return �Í�������p�����[�^���i�[����}�b�v
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
     * �Í���������̉�ₖh�~�p�n�b�V���l�𐶐�����B<p>
     *
     * @param params �Í�������p�����[�^���i�[�����}�b�v
     * @return �n�b�V���l
     */
    public String createHash(Map params){
        return createHash(encodeParams(params));
    }
    
    /**
     * �Í���������̉�ₖh�~�p�n�b�V���l�𐶐�����B<p>
     *
     * @param str �Í������镶����
     * @return �n�b�V���l
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
            // �N����Ȃ��͂�
            throw new UnexpectedCryptException(e);
        }catch(NoSuchAlgorithmException e){
            // �N����Ȃ��͂�
            throw new UnexpectedCryptException(e);
        }catch(NoSuchProviderException e){
            // �N����Ȃ��͂�
            throw new UnexpectedCryptException(e);
        }
    }
    
    /**
     * �閧���o�C�g�z��𐶐�����B<p>
     *
     * @param byteLength �o�C�g��
     * @return �閧���o�C�g�z��
     */
    public byte[] createRandomKey(int byteLength){
        return random.generateSeed(byteLength);
    }
    
    /**
     * �Í����p�����x�N�^�𐶐�����B<p>
     *
     * @return �����x�N�^
     */
    public String createInitialVector(){
        return toHexString(random.generateSeed(ivLength));
    }
    
    /**
     * �w�肳�ꂽ�p�����[�^���Í�������B<p>
     *
     * @param params �Í�������p�����[�^���i�[�����}�b�v
     * @return �Í����p�����[�^������
     */
    public String encrypt(Map params){
        return encrypt(null, params);
    }
    
    /**
     * �w�肳�ꂽ�p�����[�^���Í�������B<p>
     *
     * @param iv �����x�N�^
     * @param params �Í�������p�����[�^���i�[�����}�b�v
     * @return �Í����p�����[�^������
     */
    public String encrypt(String iv, Map params){
        return encrypt(iv, params, false);
    }
    
    /**
     * �w�肳�ꂽ�p�����[�^���Í�������B<p>
     *
     * @param iv �����x�N�^
     * @param params �Í�������p�����[�^���i�[�����}�b�v
     * @param isCreateHash ��ₖh�~�̃n�b�V���l�𐶐����邩�ǂ���
     * @return �Í����p�����[�^������
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
            // �N����Ȃ��͂�
            throw new UnexpectedCryptException(e);
        }catch(NoSuchPaddingException e){
            // �N����Ȃ��͂�
            throw new UnexpectedCryptException(e);
        }catch(NoSuchProviderException e){
            // �N����Ȃ��͂�
            throw new UnexpectedCryptException(e);
        }catch(InvalidKeyException e){
            // �N����Ȃ��͂�
            throw new UnexpectedCryptException(e);
        }catch(InvalidAlgorithmParameterException e){
            // �N����Ȃ��͂�
            throw new UnexpectedCryptException(e);
        }catch(FalsifiedParameterException e){
            // �N����Ȃ��͂�
            throw new UnexpectedCryptException(e);
        }
        
        byte[] encrypt = null;
        try{
            encrypt = c.doFinal(
                encodeParams(params).getBytes(encoding)
            );
        }catch(BadPaddingException e){
            // �Í����ł͋N����Ȃ��͂�
            throw new UnexpectedCryptException(e);
        }catch(UnsupportedEncodingException e){
            // �N����Ȃ��͂�
            throw new UnexpectedCryptException(e);
        }catch(IllegalBlockSizeException e){
            // �N����Ȃ��͂�
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
     * �w�肳�ꂽ��������Í�������B<p>
     *
     * @param str �Í������镶����
     * @return �Í���������
     */
    public String encryptString(String str){
        return encryptString(null, str);
    }
    
    /**
     * �w�肳�ꂽ��������Í�������B<p>
     *
     * @param iv �����x�N�^
     * @param str �Í������镶����
     * @return �Í���������
     */
    public String encryptString(String iv, String str){
        Map params = createParametersMap();
        params.put(SINGLE_KEY, str);
        return encrypt(iv, params);
    }
    
    /**
     * �w�肳�ꂽ�p�����[�^�𕜍�������B<p>
     * {@link #decrypt(String, String, String) decrypt(null, params, null)}�ŌĂяo���̂Ɠ����ł���B<br>
     *
     * @param params �Í����p�����[�^������
     * @return ���������ꂽ�p�����[�^���i�[���ꂽ�}�b�v
     * @exception FalsifiedParameterException �p�����[�^����₂���Ă����ꍇ
     * @see #decrypt(String, String, String)
     */
    public Map decrypt(String params) throws FalsifiedParameterException{
        try{
            return decrypt(null, params, null, -1, false);
        }catch(OverLimitExpiresException e){
            // �N����Ȃ�
            throw new UnexpectedCryptException(e);
        }
    }
    
    /**
     * �w�肳�ꂽ�p�����[�^�𕜍�������B<p>
     * {@link #decrypt(String, String, String) decrypt(iv, params, null)}�ŌĂяo���̂Ɠ����ł���B<br>
     *
     * @param iv �����x�N�^
     * @param params �Í����p�����[�^������
     * @return ���������ꂽ�p�����[�^���i�[���ꂽ�}�b�v
     * @exception FalsifiedParameterException �p�����[�^����₂���Ă����ꍇ
     * @see #decrypt(String, String, String)
     */
    public Map decrypt(
        String iv,
        String params
    ) throws FalsifiedParameterException{
        try{
            return decrypt(iv, params, null, -1, false);
        }catch(OverLimitExpiresException e){
            // �N����Ȃ�
            throw new UnexpectedCryptException(e);
        }
    }
    
    /**
     * �w�肳�ꂽ�p�����[�^�𕜍�������B�����ɗL�������`�F�b�N���s���B<p>
     * �����������p�����[�^��{@link #createParametersMap()}�ō��ꂽ�}�b�v�łȂ��ꍇ��Aexpires��0�ȉ��̒l���w�肳��Ă���ꍇ�́A�L�������`�F�b�N���s��Ȃ��B<br>
     *
     * @param params �Í����p�����[�^������
     * @param expires �L������[msec]
     * @return ���������ꂽ�p�����[�^���i�[���ꂽ�}�b�v
     * @exception OverLimitExpiresException �Í����p�����[�^������̗L���������߂��Ă��܂����ꍇ
     * @exception FalsifiedParameterException �p�����[�^����₂���Ă����ꍇ
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
     * �w�肳�ꂽ�p�����[�^�𕜍�������B�����ɗL�������`�F�b�N���s���B<p>
     * �����������p�����[�^��{@link #createParametersMap()}�ō��ꂽ�}�b�v�łȂ��ꍇ��Aexpires��0�ȉ��̒l���w�肳��Ă���ꍇ�́A�L�������`�F�b�N���s��Ȃ��B<br>
     *
     * @param iv �����x�N�^
     * @param params �Í����p�����[�^������
     * @param expires �L������[msec]
     * @return ���������ꂽ�p�����[�^���i�[���ꂽ�}�b�v
     * @exception OverLimitExpiresException �Í����p�����[�^������̗L���������߂��Ă��܂����ꍇ
     * @exception FalsifiedParameterException �p�����[�^����₂���Ă����ꍇ
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
     * �w�肳�ꂽ�p�����[�^�𕜍�������B�����ɉ�₃`�F�b�N�y�їL�������`�F�b�N���s���B<p>
     * hash��null�̏ꍇ�́AFalsifiedParameterException��throw����B<br>
     * �܂��A�Í����p�����[�^������̗L�������̃`�F�b�N�͍s��Ȃ��B<br>
     * {@link #decrypt(String, String, String, long) decrypt(iv, params, hash, encoding, -1)}�ŌĂяo���̂Ɠ����ł���B<br>
     *
     * @param iv �����x�N�^
     * @param params �Í����p�����[�^������
     * @param hash �Í����O�̃n�b�V���l
     * @return ���������ꂽ�p�����[�^���i�[���ꂽ�}�b�v
     * @exception FalsifiedParameterException �p�����[�^����₂���Ă����ꍇ
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
            // �N����Ȃ�
            throw new UnexpectedCryptException(e);
        }
    }
    
    /**
     * �w�肳�ꂽ�p�����[�^�𕜍�������B�����ɉ�₃`�F�b�N�y�їL�������`�F�b�N���s���B<p>
     * hash��null�̏ꍇ�́AFalsifiedParameterException��throw����B<br>
     * �܂��A�����������p�����[�^��{@link #createParametersMap()}�ō��ꂽ�}�b�v�łȂ��ꍇ��Aexpires��0�ȉ��̒l���w�肳��Ă���ꍇ�́A�L�������`�F�b�N���s��Ȃ��B<br>
     *
     * @param iv �����x�N�^
     * @param params �Í����p�����[�^������
     * @param hash �Í����O�̃n�b�V���l
     * @param expires �L������[msec]
     * @return ���������ꂽ�p�����[�^���i�[���ꂽ�}�b�v
     * @exception FalsifiedParameterException �p�����[�^����₂���Ă����ꍇ
     * @exception OverLimitExpiresException �Í����p�����[�^������̗L���������߂��Ă��܂����ꍇ
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
            // �N����Ȃ��͂�
            throw new UnexpectedCryptException(e);
        }catch(NoSuchPaddingException e){
            // �N����Ȃ��͂�
            throw new UnexpectedCryptException(e);
        }catch(NoSuchProviderException e){
            // �N����Ȃ��͂�
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
            // �N����Ȃ��͂�
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
            // �N����Ȃ��͂�
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
     * �w�肳�ꂽ������𕜍�������B<p>
     * {@link #decryptString(String, String, String) decryptString(null, params, null)}�ŌĂяo���̂Ɠ����ł���B<br>
     *
     * @param str �Í���������
     * @return ���������ꂽ������
     * @exception FalsifiedParameterException �Í��������񂪉�₂���Ă����ꍇ
     * @see #decryptString(String, String, String)
     */
    public String decryptString(String str) throws FalsifiedParameterException{
        try{
            return decryptString(null, str, null, -1, false);
        }catch(OverLimitExpiresException e){
            // �N����Ȃ�
            throw new UnexpectedCryptException(e);
        }
    }
    
    /**
     * �w�肳�ꂽ������𕜍�������B<p>
     * {@link #decryptString(String, String, String) decryptString(iv, params, null)}�ŌĂяo���̂Ɠ����ł���B<br>
     *
     * @param iv �����x�N�^
     * @param str �Í���������
     * @return ���������ꂽ������
     * @exception FalsifiedParameterException �Í��������񂪉�₂���Ă����ꍇ
     * @see #decryptString(String, String, String)
     */
    public String decryptString(
        String iv,
        String str
    ) throws FalsifiedParameterException{
        try{
            return decryptString(iv, str, null, -1, false);
        }catch(OverLimitExpiresException e){
            // �N����Ȃ�
            throw new UnexpectedCryptException(e);
        }
    }
    
    /**
     * �w�肳�ꂽ������𕜍�������B�����ɗL�������`�F�b�N���s���B<p>
     * expires��0�ȉ��̒l���w�肳��Ă���ꍇ�́A�L�������`�F�b�N���s��Ȃ��B<br>
     *
     * @param str �Í���������
     * @param expires �L������[msec]
     * @return ���������ꂽ������
     * @exception OverLimitExpiresException �Í���������̗L���������߂��Ă��܂����ꍇ
     * @exception FalsifiedParameterException �Í��������񂪉�₂���Ă����ꍇ
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
     * �w�肳�ꂽ�Í���������𕜍�������B�����ɗL�������`�F�b�N���s���B<p>
     * expires��0�ȉ��̒l���w�肳��Ă���ꍇ�́A�L�������`�F�b�N���s��Ȃ��B<br>
     *
     * @param iv �����x�N�^
     * @param str �Í���������
     * @param expires �L������[msec]
     * @return ���������ꂽ������
     * @exception OverLimitExpiresException �Í���������̗L���������߂��Ă��܂����ꍇ
     * @exception FalsifiedParameterException �Í��������񂪉�₂���Ă����ꍇ
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
     * �w�肳�ꂽ������𕜍�������B�����ɉ�₃`�F�b�N�y�їL�������`�F�b�N���s���B<p>
     * hash��null�̏ꍇ�́AFalsifiedParameterException��throw����B<br>
     * �܂��A�Í���������̗L�������̃`�F�b�N�͍s��Ȃ��B<br>
     * {@link #decryptString(String, String, String, long) decryptString(iv, params, hash, encoding, -1)}�ŌĂяo���̂Ɠ����ł���B<br>
     *
     * @param iv �����x�N�^
     * @param str �Í���������
     * @param hash �Í����O�̃n�b�V���l
     * @return ���������ꂽ������
     * @exception FalsifiedParameterException �Í��������񂪉�₂���Ă����ꍇ
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
            // �N����Ȃ�
            throw new UnexpectedCryptException(e);
        }
    }
    
    /**
     * �w�肳�ꂽ������𕜍�������B�����ɉ�₃`�F�b�N�y�їL�������`�F�b�N���s���B<p>
     * hash��null�̏ꍇ�́AFalsifiedParameterException��throw����B<br>
     * �܂��Aexpires��0�ȉ��̒l���w�肳��Ă���ꍇ�́A�L�������`�F�b�N���s��Ȃ��B<br>
     *
     * @param iv �����x�N�^
     * @param str �Í���������
     * @param hash �Í����O�̃n�b�V���l
     * @param expires �L������[msec]
     * @return ���������ꂽ������
     * @exception FalsifiedParameterException �Í��������񂪉�₂���Ă����ꍇ
     * @exception OverLimitExpiresException �Í���������̗L���������߂��Ă��܂����ꍇ
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