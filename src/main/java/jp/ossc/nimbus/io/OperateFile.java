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
package jp.ossc.nimbus.io;

import java.io.*;

/**
 * �t�@�C������t�@�C���B<p>
 * �t�@�C���̃R�s�[�╪���ƌ�����{@link File}�ɂȂ��t�@�C��������s���B<br>
 *
 * @author H.Nakano
 */
public class OperateFile extends File implements Serializable{
    
    private static final long serialVersionUID = -3537857563620684853L;
    
    /**
     * �w�肳�ꂽ�p�X�̃t�@�C���𑀍삷��C���X�^���X�𐶐�����B<p>
     *
     * @param file �t�@�C��
     */
    public OperateFile(File file) {
        super(file.getPath());
    }
    
    /**
     * �w�肳�ꂽ�p�X�̃t�@�C���𑀍삷��C���X�^���X�𐶐�����B<p>
     *
     * @param pathname �p�X
     */
    public OperateFile(String pathname){
        super(pathname);
    }
    
    /**
     * �w�肳�ꂽ�p�X�̃t�@�C���𑀍삷��C���X�^���X�𐶐�����B<p>
     *
     * @param parent �e�p�X
     * @param child �q�p�X
     */
    public OperateFile(String parent, String child){
        super(parent, child);
    }
    
    /**
     * �w�肳�ꂽ�p�X�̃t�@�C���𑀍삷��C���X�^���X�𐶐�����B<p>
     *
     * @param parent �e�p�X
     * @param child �q�p�X
     */
    public OperateFile(File parent, String child) {
        super(parent, child);
    }
    
    /**
     * ���̃t�@�C���̓��e���w��t�@�C���֒ǉ��������݂���B<p>
     * 
     * @param filePath �ǉ��������ݐ�̃t�@�C���p�X
     * @exception IOException ���̃t�@�C���̓ǂݍ��݁A�܂��͎w��t�@�C���ւ̏������݂Ɏ��s�����ꍇ
     */
    public void appendTo(String filePath) throws IOException{
        final File toFile = new File(filePath);
        if(!exists()){
            throw new FileNotFoundException(getAbsolutePath());
        }
        if(!toFile.exists()){
            toFile.createNewFile();
        }
        dataMove(toFile, true);
    }
    
    /**
     * ���̃t�@�C���̓��e���w��t�@�C���ɃR�s�[����B<p>
     * 
     * @param filePath �R�s�[��̃t�@�C���p�X
     * @exception IOException ���̃t�@�C���̓ǂݍ��݁A�܂��͎w��t�@�C���ւ̏������݂Ɏ��s�����ꍇ
     */
    public void copyTo(String filePath) throws IOException{
        File toFile = new File(filePath) ;
        if(!exists()){
            throw new FileNotFoundException(getAbsolutePath());
        }
        if(toFile.exists()){
            toFile.delete();
        }
        toFile.createNewFile();
        dataMove(toFile, false);
    }
    
    /**
     * ���̃t�@�C���ȉ���S�č폜����B<p>
     *
     * @return �S�č폜�ł����ꍇtrue
     */
    public boolean deleteAll(){
        return deleteAll(this);
    }
    
    /**
     * �w�肳�ꂽ�t�@�C���ȉ���S�č폜����B<p>
     *
     * @param file �폜����t�@�C��
     * @return �S�č폜�ł����ꍇtrue
     */
    public static boolean deleteAll(File file){
        if(!file.exists()){
            return true;
        }
        boolean result = true;
        if(file.isDirectory()){
            File[] files = file.listFiles();
            for(int i = 0; i < files.length; i++){
                result &= deleteAll(files[i]);
            }
            result &= file.delete();
        }else if(file.isFile()){
            result &= file.delete();
        }
        return result;
    }
    
    /**
     * ���̃t�@�C���̓��e���w��t�@�C���ɏ������ށB<p>
     *
     * @param toFile �������ݐ�̃t�@�C��
     * @exception IOException ���̃t�@�C���̓ǂݍ��݁A�܂��͎w��t�@�C���ւ̏������݂Ɏ��s�����ꍇ
     */
    private void dataMove(File toFile, boolean append) throws IOException{
        InputStream is = null;
        BufferedInputStream bis = null; 
        FileOutputStream fos = null;
        try{
            is = toURL().openStream();
            bis = new BufferedInputStream(is);
            fos = new FileOutputStream(toFile, append);
            final byte[] bytes = new byte[1024];
            int length = 0;
            while((length = bis.read(bytes, 0, 1024)) != -1){
                fos.write(bytes, 0, length);
            }
        }finally{
            if(is != null){
                try{
                    is.close();
                }catch(IOException e){}
            }
            if(bis != null){
                try{
                    bis.close();
                }catch(IOException e){}
            }
            if(fos != null){
                try{
                    fos.close();
                }catch(IOException e){}
            }
        }
    }
    
    /**
     * ���̃t�@�C���𕡐��t�@�C���ɕ�������B<p>
     * {@link #splitFile(String, String, String, int, int) splitFile(null, null, null, splitSize, startIndex)}���Ăяo���̂Ɠ����B
     *
     * @param splitSize �����T�C�Y
     * @param startIndex �����t�@�C�����̊J�n�ԍ�
     * @exception IOException ���̃t�@�C���̓ǂݍ��݁A�܂��͕����t�@�C���ւ̏������݂Ɏ��s�����ꍇ
     */
    public void splitFile(
        int splitSize,
        int startIndex
    ) throws IOException{
        splitFile(null, null, null, splitSize, startIndex);
    }
    
    /**
     * ���̃t�@�C���𕡐��t�@�C���ɕ�������B<p>
     * {@link #splitFile(String, String, String, int, int) splitFile(dir, null, null, splitSize, startIndex)}���Ăяo���̂Ɠ����B
     *
     * @param dir �����t�@�C���̊i�[��f�B���N�g���Bnull�̏ꍇ�́A���̃t�@�C���Ɠ����ꏊ�Ɋi�[�����B
     * @param startIndex �����t�@�C�����̊J�n�ԍ�
     * @exception IOException ���̃t�@�C���̓ǂݍ��݁A�܂��͕����t�@�C���ւ̏������݂Ɏ��s�����ꍇ
     */
    public void splitFile(
        String dir,
        int splitSize,
        int startIndex
    ) throws IOException{
        splitFile(dir, null, null, splitSize, startIndex);
    }
    
    /**
     * ���̃t�@�C���𕡐��t�@�C���ɕ�������B<p>
     * {@link #splitFile(String, String, String, int, int) splitFile(null, prefix, suffix, splitSize, startIndex)}���Ăяo���̂Ɠ����B
     *
     * @param prefix �����t�@�C�����̃v���t�B�N�X�Bnull�̏ꍇ�́A���̃t�@�C���̊g���q���������t�@�C�������K�p�����B
     * @param splitSize �����T�C�Y
     * @param startIndex �����t�@�C�����̊J�n�ԍ�
     * @exception IOException ���̃t�@�C���̓ǂݍ��݁A�܂��͕����t�@�C���ւ̏������݂Ɏ��s�����ꍇ
     */
    public void splitFile(
        String prefix,
        String suffix,
        int splitSize,
        int startIndex
    ) throws IOException{
        splitFile(null, prefix, suffix, splitSize, startIndex);
    }
    
    /**
     * ���̃t�@�C���𕡐��t�@�C���ɕ�������B<p>
     *
     * @param dir �����t�@�C���̊i�[��f�B���N�g���Bnull�̏ꍇ�́A���̃t�@�C���Ɠ����ꏊ�Ɋi�[�����B
     * @param prefix �����t�@�C�����̃v���t�B�N�X�Bnull�̏ꍇ�́A���̃t�@�C���̊g���q���������t�@�C�������K�p�����B
     * @param splitSize �����T�C�Y
     * @param startIndex �����t�@�C�����̊J�n�ԍ�
     * @exception IOException ���̃t�@�C���̓ǂݍ��݁A�܂��͕����t�@�C���ւ̏������݂Ɏ��s�����ꍇ
     */
    public void splitFile(
        String dir,
        String prefix,
        String suffix,
        int splitSize,
        int startIndex
    ) throws IOException{
        InputStream is = null;
        BufferedInputStream bis = null;
        FileOutputStream fos = null;
        int readedSize = -1;
        int readSize = -1;
        File toFile = null;
        if(!exists()){
            throw new FileNotFoundException(getAbsolutePath());
        }
        String tmpPath = dir;
        if(tmpPath == null && getParentFile() != null){
            tmpPath = getParentFile().getAbsolutePath();
        }
        final File tmpDir = new File(tmpPath);
        if(!tmpDir.exists()){
            tmpDir.mkdirs();
        }
        String tmpPrefix = prefix;
        String tmpSuffix = suffix;
        if(tmpPrefix == null){
            tmpPrefix = getName();
            final int index = tmpPrefix.lastIndexOf('.');
            if(index != -1){
                tmpPrefix = tmpPrefix.substring(0, index);
            }
        }
        if(tmpSuffix == null){
            final int index = getName().lastIndexOf('.');
            if(index != -1 && index == getName().length() - 1){
                tmpSuffix = getName().substring(index + 1);
            }
        }
        try{
            is = toURL().openStream();
            bis = new BufferedInputStream(is);
            boolean isEOF = false;
            int index = startIndex;
            final byte[] bytes = new byte[1024];
            while(!isEOF){
                if(readedSize == -1){
                    //�R�s�[�t�@�C�����쐬
                    final StringBuilder fileName = new StringBuilder(tmpPrefix);
                    fileName.append(index);
                    if(tmpSuffix != null){
                        fileName.append(tmpSuffix);
                    }
                    toFile = new File(tmpPath, fileName.toString());
                    if(toFile.exists()){
                        toFile.delete();
                    }
                    index++;
                    readedSize = 0;
                    fos = new FileOutputStream(toFile);
                }
                //�ǂݍ��݃T�C�Y�v�Z
                if(splitSize - readedSize < 1024){
                    readSize = splitSize - readedSize;
                }else{
                    readSize = 1024;
                }
                final int length = bis.read(bytes, 0, readSize);
                isEOF = length == -1;
                if(!isEOF){
                    fos.write(bytes, 0, length);
                    readedSize += length;
                    if(readedSize >= splitSize){
                        fos.close() ;
                        fos = null;
                        readedSize = -1;
                    }
                }else{
                    fos.close();
                    fos = null;
                }
            }
        }finally{
            if(bis != null){
                try{
                    bis.close();
                }catch(IOException e){}
            }
            if(fos != null){
                try{
                    fos.close();
                }catch(IOException e){}
            }
        }
    }
}
