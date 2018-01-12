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
package jp.ossc.nimbus.service.scheduler2;

import java.io.File;
import java.util.Date;

import jp.ossc.nimbus.core.ServiceBaseMBean;

/**
 * {@link FileConcentrateBackupManagerService}のMBeanインタフェース。<p>
 * 
 * @author M.Takata
 */
public interface FileConcentrateBackupManagerServiceMBean extends ServiceBaseMBean{
    
    /**
     * デフォルトの日付ディレクトリフォーマット。<p>
     */
    public static final String DEFAULT_DATE_FORMAT = "yyyyMMdd";
    
    /**
     * 圧縮モード：非圧縮。<p>
     */
    public static final int COMPRESS_MODE_NONE = 0;
    
    /**
     * 圧縮モード：ZLIB形式。<p>
     */
    public static final int COMPRESS_MODE_ZLIB = 1;
    
    /**
     * 圧縮モード：ZIP形式。<p>
     */
    public static final int COMPRESS_MODE_ZIP = 2;
    
    /**
     * 圧縮モード：GZIP形式。<p>
     */
    public static final int COMPRESS_MODE_GZIP = 3;
    
    /**
     * 日付ディレクトリのフォーマットを設定する。<p>
     * デフォルトは、{@link #DEFAULT_DATE_FORMAT}。<br>
     * 
     * @param format フォーマット
     */
    public void setDateFormat(String format);
    
    /**
     * 日付ディレクトリのフォーマットを取得する。<p>
     * 
     * @return フォーマット
     */
    public String getDateFormat();
    
    /**
     * バックアップディレクトリを設定する。<p>
     * デフォルトは、"backup"。<br>
     *
     * @param dir バックアップディレクトリ
     */
    public void setBackupDirectory(File dir);
    
    /**
     * バックアップディレクトリを取得する。<p>
     *
     * @return バックアップディレクトリ
     */
    public File getBackupDirectory();
    
    /**
     * バックアップ時の読み込みストリームのバッファサイズを設定する。<p>
     * デフォルトは、1024。<br>
     *
     * @param size バッファサイズ
     */
    public void setBufferSize(int size);
    
    /**
     * バックアップ時の読み込みストリームのバッファサイズを取得する。<p>
     *
     * @return バッファサイズ
     */
    public int getBufferSize();
    
    /**
     * 圧縮モードを設定する。<p>
     * デフォルトは、{@link #COMPRESS_MODE_NONE 非圧縮}。<br>
     * 
     * @param mode 圧縮モード
     * @see #COMPRESS_MODE_NONE
     * @see #COMPRESS_MODE_ZLIB
     * @see #COMPRESS_MODE_ZIP
     * @see #COMPRESS_MODE_GZIP
     */
    public void setCompressMode(int mode);
    
    /**
     * 圧縮モードを取得する。<p>
     * 
     * @return 圧縮モード
     */
    public int getCompressMode();
    
    /**
     * 圧縮レベルを設定する。<p>
     * デフォルトは、{@link java.util.zip.Deflater#DEFAULT_COMPRESSION}。<br>
     * 圧縮モードが、{@link #COMPRESS_MODE_ZLIB}、{@link #COMPRESS_MODE_ZIP}の場合、有効。<br>
     * 
     * @param level 圧縮レベル
     */
    public void setCompressLevel(int level);
    
    /**
     * 圧縮レベルを取得する。<p>
     * 
     * @return 圧縮レベル
     */
    public int getCompressLevel();
    
    /**
     * 圧縮メソッドを設定する。<p>
     * デフォルトは、{@link java.util.zip.ZipOutputStream#DEFLATED}。<br>
     * 圧縮モードが、{@link #COMPRESS_MODE_ZIP}の場合のみ、有効。<br>
     * 
     * @param method 圧縮メソッド
     */
    public void setCompressMethod(int method);
    
    /**
     * 圧縮メソッドを取得する。<p>
     * 
     * @return 圧縮メソッド
     */
    public int getCompressMethod();
    
    /**
     * バックアップを全て削除する。<p>
     *
     * @return 削除できた場合は、true
     * @exception ConcentrateBackupException バックアップ削除中に異常が発生した場合
     */
    public boolean clear() throws ConcentrateBackupException;
    
    /**
     * 指定したバックアップグループのバックアップを全て削除する。<p>
     *
     * @param group バックアップグループ名
     * @return 削除できた場合は、true
     * @exception ConcentrateBackupException バックアップ削除中に異常が発生した場合
     */
    public boolean remove(String group) throws ConcentrateBackupException;
    
    /**
     * 指定したバックアップ日付のバックアップを全て削除する。<p>
     *
     * @param date バックアップ日付
     * @return 削除できた場合は、true
     * @exception ConcentrateBackupException バックアップ削除中に異常が発生した場合
     */
    public boolean remove(Date date) throws ConcentrateBackupException;
    
    /**
     * 指定したバックアップグループ且つバックアップ日付のバックアップを全て削除する。<p>
     *
     * @param group バックアップグループ名
     * @param date バックアップ日付
     * @return 削除できた場合は、true
     * @exception ConcentrateBackupException バックアップ削除中に異常が発生した場合
     */
    public boolean remove(String group, Date date) throws ConcentrateBackupException;
    
    /**
     * 指定したバックアップ日付までのバックアップを全て削除する。<p>
     *
     * @param date バックアップ日付
     * @return 削除できた場合は、true
     * @exception ConcentrateBackupException バックアップ削除中に異常が発生した場合
     */
    public boolean removeTo(Date date) throws ConcentrateBackupException;
    
    /**
     * 指定したバックアップグループ且つバックアップ日付までのバックアップを全て削除する。<p>
     *
     * @param group バックアップグループ名
     * @param date バックアップ日付
     * @return 削除できた場合は、true
     * @exception ConcentrateBackupException バックアップ削除中に異常が発生した場合
     */
    public boolean removeTo(String group, Date date) throws ConcentrateBackupException;
    
    /**
     * 指定したバックアップグループ、バックアップ日付、バックアップキーのバックアップを全て削除する。<p>
     *
     * @param group バックアップグループ名
     * @param date バックアップ日付
     * @param key バックアップキー
     * @return 削除できた場合は、true
     * @exception ConcentrateBackupException バックアップ削除中に異常が発生した場合
     */
    public boolean remove(String group, Date date, String key) throws ConcentrateBackupException;
}