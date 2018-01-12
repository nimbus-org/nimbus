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

import java.util.Date;
import java.io.File;

/**
 * 集配信バックアップ管理。<p>
 * 
 * @author M.Takata
 */
public interface ConcentrateBackupManager{
    
    
    /**
     * 指定されたファイルをバックアップする。<br>
     *
     * @param group バックアップグループ名
     * @param date バックアップ日付
     * @param key バックアップキー
     * @param file バックアップ対象のファイル
     * @param compressed バックアップ対象の各ファイルが圧縮されているかどうかを示すフラグ
     * @return バックアップ情報
     * @exception ConcentrateBackupException バックアップ中に異常が発生した場合
     */
    public Object backup(String group, Date date, String key, File file, boolean compressed) throws ConcentrateBackupException;
    
    /**
     * 指定されたファイルをバックアップする。<br>
     *
     * @param group バックアップグループ名
     * @param date バックアップ日付
     * @param key バックアップキー
     * @param file バックアップ対象のファイル
     * @param compressed バックアップ対象の各ファイルが圧縮されているかどうかを示すフラグ
     * @param result バックアップ情報。繰り返し呼び出し用。
     * @return バックアップ情報
     * @exception ConcentrateBackupException バックアップ中に異常が発生した場合
     */
    public Object backup(String group, Date date, String key, File file, boolean compressed, Object result) throws ConcentrateBackupException;
    
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
