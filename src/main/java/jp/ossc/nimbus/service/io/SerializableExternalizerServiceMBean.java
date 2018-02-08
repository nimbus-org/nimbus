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
 * {@link SerializableExternalizerService}��MBean�C���^�t�F�[�X<p>
 * 
 * @author M.Takata
 * @see SerializableExternalizerService
 */
public interface SerializableExternalizerServiceMBean extends ServiceBaseMBean{
    
    /**
     * ���k���[�h�F�񈳏k�B<p>
     */
    public static final int COMPRESS_MODE_NONE = 0;
    
    /**
     * ���k���[�h�FZLIB�`���B<p>
     */
    public static final int COMPRESS_MODE_ZLIB = 1;
    
    /**
     * ���k���[�h�FZIP�`���B<p>
     */
    public static final int COMPRESS_MODE_ZIP = 2;
    
    /**
     * ���k���[�h�FGZIP�`���B<p>
     */
    public static final int COMPRESS_MODE_GZIP = 3;
    
    /**
     * ���k���[�h�FSNAPPY�`���B<p>
     */

    public static final int COMPRESS_MODE_SNAPPY = 4;

    
    /**
     * ���k���[�h�FLZ4�`���B<p>
     */

    public static final int COMPRESS_MODE_LZ4 = 5;

    
    /**
     * ���k���[�h��ݒ肷��B<p>
     * �f�t�H���g�́A{@link #COMPRESS_MODE_NONE �񈳏k}�B<br>
     * 
     * @param mode ���k���[�h
     * @see #COMPRESS_MODE_NONE
     * @see #COMPRESS_MODE_ZLIB
     * @see #COMPRESS_MODE_ZIP
     * @see #COMPRESS_MODE_GZIP
     * @see #COMPRESS_MODE_SNAPPY
     * @see #COMPRESS_MODE_LZ4
     */
    public void setCompressMode(int mode);
    
    /**
     * ���k���[�h���擾����B<p>
     * 
     * @return ���k���[�h
     */
    public int getCompressMode();
    
    /**
     * ���k���x����ݒ肷��B<p>
     * �f�t�H���g�́A{@link java.util.zip.Deflater#DEFAULT_COMPRESSION}�B<br>
     * ���k���[�h���A{@link #COMPRESS_MODE_ZLIB}�A{@link #COMPRESS_MODE_ZIP}�̏ꍇ�A�L���B<br>
     * 
     * @param level ���k���x��
     */
    public void setCompressLevel(int level);
    
    /**
     * ���k���x�����擾����B<p>
     * 
     * @return ���k���x��
     */
    public int getCompressLevel();
    
    /**
     * ���k���\�b�h��ݒ肷��B<p>
     * �f�t�H���g�́A{@link java.util.zip.ZipOutputStream#DEFLATED}�B<br>
     * ���k���[�h���A{@link #COMPRESS_MODE_ZIP}�̏ꍇ�̂݁A�L���B<br>
     * 
     * @param method ���k���\�b�h
     */
    public void setCompressMethod(int method);
    
    /**
     * ���k���\�b�h���擾����B<p>
     * 
     * @return ���k���\�b�h
     */
    public int getCompressMethod();
    
    /**
     * ���k臒l��ݒ肷��B<p>
     * ���k臒l�𒴂���o�C�g���ƂȂ�I�u�W�F�N�g�݈̂��k����B<br>
     * �f�t�H���g�́A-1�őS�Ĉ��k����B<br>
     *
     * @param threshold ���k臒l[byte]
     */
    public void setCompressThreshold(int threshold);
    
    /**
     * ���k臒l���擾����B<p>
     * 
     * @return ���k臒l[byte]
     */
    public int getCompressThreshold();
    
    /**
     * ���k/�𓀎��̓��o�͂̃o�b�t�@�T�C�Y��ݒ肷��B<p>
     * 
     * @param size �o�b�t�@�T�C�Y
     */
    public void setBufferSize(int size);
    
    /**
     * ���k/�𓀎��̓��o�͂̃o�b�t�@�T�C�Y���擾����B<p>
     * 
     * @return �o�b�t�@�T�C�Y
     */
    public int getBufferSize();
    
    /**
     * �o�̓X�g���[�����o�b�t�@�����O���邩�ǂ����𔻒肷��B<p>
     * 
     * @return true�̏ꍇ�A�o�b�t�@�����O����
     */
    public boolean isBufferedOutputStream();
    
    /**
     * �o�̓X�g���[�����o�b�t�@�����O���邩�ǂ�����ݒ肷��B<p>
     * �f�t�H���g�́Afalse�Ńo�b�t�@�����O���Ȃ��B
     * 
     * @param isBuffered �o�b�t�@�����O����ꍇ�Atrue
     */
    public void setBufferedOutputStream(boolean isBuffered);
    
    /**
     * �o�̓X�g���[�����o�b�t�@�����O����ꍇ�̏����o�b�t�@�T�C�Y��ݒ肷��B<p>
     * �f�t�H���g�́A1024�B
     * 
     * @param size �����o�b�t�@�T�C�Y
     */
    public void setOutputStreamInitialBufferSize(int size);
    
    /**
     * �o�̓X�g���[�����o�b�t�@�����O����ꍇ�̏����o�b�t�@�T�C�Y���擾����B<p>
     * 
     * @return �����o�b�t�@�T�C�Y
     */
    public int getOutputStreamInitialBufferSize();
    
    /**
     * �o�̓X�g���[�����o�b�t�@�����O����ꍇ�̃o�b�t�@�T�C�Y�g���{����ݒ肷��B<p>
     * �o�b�t�@���͊����ē����I�Ƀt���b�V������ۂɁA���̔{���Ńo�b�t�@�T�C�Y���g������B<br>
     * �f�t�H���g�́A2�B<br>
     * 
     * @param ratio �g���{��
     */
    public void setOutputStreamBufferExpandRatio(float ratio);
    
    /**
     * �o�̓X�g���[�����o�b�t�@�����O����ꍇ�̃o�b�t�@�T�C�Y�g���{�����擾����B<p>
     * 
     * @return �g���{��
     */
    public float getOutputStreamBufferExpandRatio();
    
    /**
     * �o�̓X�g���[�����o�b�t�@�����O����ꍇ�̍ő�o�b�t�@�T�C�Y��ݒ肷��B<p>
     * �o�b�t�@���͊����ē����I�Ƀt���b�V������ۂɁA�o�b�t�@�T�C�Y���g�����邪�A�ő�ł��̃T�C�Y�܂Ŋg������B<br>
     * �f�t�H���g�́A10240�B<br>
     * 
     * @param size �ő�o�b�t�@�T�C�Y
     */
    public void setOutputStreamMaxBufferSize(int size);
    
    /**
     * �o�̓X�g���[�����o�b�t�@�����O����ꍇ�̍ő�o�b�t�@�T�C�Y���擾����B<p>
     * 
     * @return �ő�o�b�t�@�T�C�Y
     */
    public int getOutputStreamMaxBufferSize();
    
    /**
     * {@link Externalizer#writeExternal(Object, java.io.OutputStream)}���Ăяo���ꂽ�ۂɁAjava.io.OutputStream�����b�v����java.io.ObjectOutput�̎����N���X��ݒ肷��B<p>
     * �����ŁA�w�肷��java.io.ObjectOutput�̎����N���X�́A������java.io.OutputStream�����R���X�g���N�^�����K�v������B<br>
     * �f�t�H���g�́Anull�ŁAjava.io.ObjectOutputStream���g�p�����B<br>
     *
     * @param clazz java.io.ObjectOutput�̎����N���X
     */
    public void setObjectOutputClass(Class clazz);
    
    /**
     * {@link Externalizer#writeExternal(Object, java.io.OutputStream)}���Ăяo���ꂽ�ۂɁAjava.io.OutputStream�����b�v����java.io.ObjectOutput�̎����N���X���擾����B<p>
     *
     * @return java.io.ObjectOutput�̎����N���X
     */
    public Class getObjectOutputClass();
    
    /**
     * {@link Externalizer#readExternal(java.io.InputStream)}���Ăяo���ꂽ�ۂɁAjava.io.InputStream�����b�v����java.io.ObjectInput�̎����N���X��ݒ肷��B<p>
     * �����ŁA�w�肷��java.io.ObjectInput�̎����N���X�́A������java.io.InputStream�����R���X�g���N�^�����K�v������B<br>
     * �f�t�H���g�́Anull�ŁAjava.io.ObjectInputStream���g�p�����B<br>
     *
     * @param clazz java.io.ObjectInput�̎����N���X
     */
    public void setObjectInputClass(Class clazz);
    
    /**
     * {@link Externalizer#readExternal(java.io.InputStream)}���Ăяo���ꂽ�ۂɁAjava.io.InputStream�����b�v����java.io.ObjectInput�̎����N���X���擾����B<p>
     *
     * @return java.io.ObjectInput�̎����N���X
     */
    public Class getObjectInputClass();
}
