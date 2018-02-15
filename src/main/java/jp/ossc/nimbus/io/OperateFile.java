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
 * ファイル操作ファイル。<p>
 * ファイルのコピーや分割と言った{@link File}にないファイル操作を行う。<br>
 *
 * @author H.Nakano
 */
public class OperateFile extends File implements Serializable{
    
    private static final long serialVersionUID = -3537857563620684853L;
    
    /**
     * 指定されたパスのファイルを操作するインスタンスを生成する。<p>
     *
     * @param file ファイル
     */
    public OperateFile(File file) {
        super(file.getPath());
    }
    
    /**
     * 指定されたパスのファイルを操作するインスタンスを生成する。<p>
     *
     * @param pathname パス
     */
    public OperateFile(String pathname){
        super(pathname);
    }
    
    /**
     * 指定されたパスのファイルを操作するインスタンスを生成する。<p>
     *
     * @param parent 親パス
     * @param child 子パス
     */
    public OperateFile(String parent, String child){
        super(parent, child);
    }
    
    /**
     * 指定されたパスのファイルを操作するインスタンスを生成する。<p>
     *
     * @param parent 親パス
     * @param child 子パス
     */
    public OperateFile(File parent, String child) {
        super(parent, child);
    }
    
    /**
     * このファイルの内容を指定ファイルへ追加書き込みする。<p>
     * 
     * @param filePath 追加書き込み先のファイルパス
     * @exception IOException このファイルの読み込み、または指定ファイルへの書き込みに失敗した場合
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
     * このファイルの内容を指定ファイルにコピーする。<p>
     * 
     * @param filePath コピー先のファイルパス
     * @exception IOException このファイルの読み込み、または指定ファイルへの書き込みに失敗した場合
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
     * このファイル以下を全て削除する。<p>
     *
     * @return 全て削除できた場合true
     */
    public boolean deleteAll(){
        return deleteAll(this);
    }
    
    /**
     * 指定されたファイル以下を全て削除する。<p>
     *
     * @param file 削除するファイル
     * @return 全て削除できた場合true
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
     * このファイルの内容を指定ファイルに書き込む。<p>
     *
     * @param toFile 書き込み先のファイル
     * @exception IOException このファイルの読み込み、または指定ファイルへの書き込みに失敗した場合
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
     * このファイルを複数ファイルに分割する。<p>
     * {@link #splitFile(String, String, String, int, int) splitFile(null, null, null, splitSize, startIndex)}を呼び出すのと同じ。
     *
     * @param splitSize 分割サイズ
     * @param startIndex 分割ファイル名の開始番号
     * @exception IOException このファイルの読み込み、または分割ファイルへの書き込みに失敗した場合
     */
    public void splitFile(
        int splitSize,
        int startIndex
    ) throws IOException{
        splitFile(null, null, null, splitSize, startIndex);
    }
    
    /**
     * このファイルを複数ファイルに分割する。<p>
     * {@link #splitFile(String, String, String, int, int) splitFile(dir, null, null, splitSize, startIndex)}を呼び出すのと同じ。
     *
     * @param dir 分割ファイルの格納先ディレクトリ。nullの場合は、このファイルと同じ場所に格納される。
     * @param startIndex 分割ファイル名の開始番号
     * @exception IOException このファイルの読み込み、または分割ファイルへの書き込みに失敗した場合
     */
    public void splitFile(
        String dir,
        int splitSize,
        int startIndex
    ) throws IOException{
        splitFile(dir, null, null, splitSize, startIndex);
    }
    
    /**
     * このファイルを複数ファイルに分割する。<p>
     * {@link #splitFile(String, String, String, int, int) splitFile(null, prefix, suffix, splitSize, startIndex)}を呼び出すのと同じ。
     *
     * @param prefix 分割ファイル名のプレフィクス。nullの場合は、このファイルの拡張子を除いたファイル名が適用される。
     * @param splitSize 分割サイズ
     * @param startIndex 分割ファイル名の開始番号
     * @exception IOException このファイルの読み込み、または分割ファイルへの書き込みに失敗した場合
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
     * このファイルを複数ファイルに分割する。<p>
     *
     * @param dir 分割ファイルの格納先ディレクトリ。nullの場合は、このファイルと同じ場所に格納される。
     * @param prefix 分割ファイル名のプレフィクス。nullの場合は、このファイルの拡張子を除いたファイル名が適用される。
     * @param splitSize 分割サイズ
     * @param startIndex 分割ファイル名の開始番号
     * @exception IOException このファイルの読み込み、または分割ファイルへの書き込みに失敗した場合
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
                    //コピーファイル名作成
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
                //読み込みサイズ計算
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
