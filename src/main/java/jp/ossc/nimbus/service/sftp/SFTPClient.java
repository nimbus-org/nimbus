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
package jp.ossc.nimbus.service.sftp;

import java.io.File;
import java.util.Date;

/**
 * SFTPクライアント。<p>
 *
 * @author M.Takata
 */
public interface SFTPClient{
    
    /**
     * サーバに接続する。<p>
     *
     * @param user ユーザ名
     * @param host 接続先サーバのホスト名
     * @param password パスワード
     * @exception SFTPException サーバとの接続に失敗した場合
     */
    public void connect(String user, String host, String password) throws SFTPException;
    
    /**
     * サーバに接続する。<p>
     *
     * @param user ユーザ名
     * @param host 接続先サーバのホスト名
     * @param port 接続先サーバのポート番号
     * @param password パスワード
     * @exception SFTPException サーバとの接続に失敗した場合
     */
    public void connect(String user, String host, int port, String password) throws SFTPException;
    
    /**
     * サーバに接続する。<p>
     *
     * @param user ユーザ名
     * @param host 接続先サーバのホスト名
     * @param pemFile 秘密鍵ファイル
     * @param passphrase パスフレーズ
     * @exception SFTPException サーバとの接続に失敗した場合
     */
    public void connect(String user, String host, File pemFile, String passphrase) throws SFTPException;
    
    /**
     * サーバに接続する。<p>
     *
     * @param user ユーザ名
     * @param host 接続先サーバのホスト名
     * @param port 接続先サーバのポート番号
     * @param pemFile 秘密鍵ファイル
     * @param passphrase パスフレーズ
     * @exception SFTPException サーバとの接続に失敗した場合
     */
    public void connect(String user, String host, int port, File pemFile, String passphrase) throws SFTPException;
    
    /**
     * サーバのファイル名一覧を取得する。<p>
     * 
     * @return ファイル名の配列
     * @exception SFTPException 取得に失敗した場合
     */
    public String[] ls() throws SFTPException;
    
    /**
     * サーバの指定されたディレクトリ内のファイル名一覧を取得する。<p>
     * 
     * @return ファイル名の配列
     * @exception SFTPException 取得に失敗した場合
     */
    public String[] ls(String path) throws SFTPException;
    
    /**
     * サーバのファイル名一覧を取得する。<p>
     * 
     * @return SFTPファイルの配列
     * @exception SFTPException 取得に失敗した場合
     */
    public SFTPFile[] lsFile() throws SFTPException;
    
    /**
     * サーバの指定されたディレクトリ内のファイル名一覧を取得する。<p>
     * 
     * @return SFTPファイルの配列
     * @exception SFTPException 取得に失敗した場合
     */
    public SFTPFile[] lsFile(String path) throws SFTPException;
    
    /**
     * サーバ側でのカレントディレクトリを取得する。<p>
     * 
     * @return カレントディレクトリのパス
     * @exception SFTPException 取得に失敗した場合
     */
    public String pwd() throws SFTPException;
    
    /**
     * クライアント側でのカレントディレクトリを取得する。<p>
     * 
     * @return カレントディレクトリ
     * @exception SFTPException 取得に失敗した場合
     */
    public File lpwd() throws SFTPException;
    
    /**
     * サーバ側でのカレントディレクトリを指定されたパスに移動する。<p>
     * 
     * @param path 移動先のパス
     * @exception SFTPException 移動に失敗した場合
     */
    public void cd(String path) throws SFTPException;
    
    /**
     * クライアント側でのカレントディレクトリを指定されたパスに移動する。<p>
     * 
     * @param path 移動先のパス
     * @exception SFTPException 移動に失敗した場合
     */
    public void lcd(String path) throws SFTPException;
    
    /**
     * サーバ側で指定されたディレクトリを作成する。<p>
     * 
     * @param dir 作成するディレクトリのパス
     * @exception SFTPException 作成に失敗した場合
     */
    public void mkdir(String dir) throws SFTPException;
    
    /**
     * サーバ側で指定されたファイルのファイル名を変更する。<p>
     * 
     * @param from 変更対象のファイルのパス
     * @param to 変更後のファイル名
     * @exception SFTPException 変更に失敗した場合
     */
    public void rename(String from, String to) throws SFTPException;
    
    /**
     * サーバ側の指定されたファイルを取得する。<p>
     * 
     * @param path 取得するファイルのパス
     * @return 取得したファイル
     * @exception SFTPException 取得に失敗した場合
     */
    public File get(String path) throws SFTPException;
    
    /**
     * サーバ側の指定されたファイルを、指定された名前のファイルとして取得する。<p>
     * 
     * @param remote 取得するファイルのパス
     * @param local 取得後のファイル名
     * @return 取得したファイル
     * @exception SFTPException 取得に失敗した場合
     */
    public File get(String remote, String local) throws SFTPException;
    
    /**
     * サーバ側の指定された全てのファイルを取得する。<p>
     * 
     * @param path 取得するファイルのパス
     * @return 取得したファイル配列
     * @exception SFTPException 取得に失敗した場合
     */
    public File[] mget(String path) throws SFTPException;
    
    /**
     * サーバ側に指定されたファイルを転送する。<p>
     * 
     * @param path 転送するファイルのパス
     * @exception SFTPException 転送に失敗した場合
     */
    public void put(String path) throws SFTPException;
    
    /**
     * サーバ側に指定されたファイルを、指定されたファイル名で転送する。<p>
     * 
     * @param local 転送するファイルのパス
     * @param remote 転送先でのファイル名
     * @exception SFTPException 転送に失敗した場合
     */
    public void put(String local, String remote) throws SFTPException;
    
    /**
     * サーバ側に指定された全てのファイルを転送する。<p>
     * 
     * @param path 転送するファイルのパス
     * @exception SFTPException 転送に失敗した場合
     */
    public void mput(String path) throws SFTPException;
    
    /**
     * サーバ側の指定されたファイルを削除する。<p>
     * 
     * @param path 削除するファイルのパス
     * @return 削除した場合true
     * @exception SFTPException 削除に失敗した場合
     */
    public boolean rm(String path) throws SFTPException;
    
    /**
     * サーバ側の指定されたディレクトリを削除する。<p>
     * 
     * @param path 削除するディレクトリのパス
     * @return 削除した場合true
     * @exception SFTPException 削除に失敗した場合
     */
    public boolean rmdir(String path) throws SFTPException;
    
    /**
     * サーバ側の指定されたファイルのパーミッションを変更する。<p>
     *
     * @param mode パーミッション
     * @param path 変更するファイルのパス
     * @exception SFTPException 変更に失敗した場合
     */
    public void chmod(String mode, String path) throws SFTPException;
    
    /**
     * サーバ側の指定されたファイルの所有者を変更する。<p>
     *
     * @param uid ユーザID
     * @param path 変更するファイルのパス
     * @exception SFTPException 変更に失敗した場合
     */
    public void chown(String uid, String path) throws SFTPException;
    
    /**
     * サーバ側の指定されたファイルのグループを変更する。<p>
     *
     * @param gid グループID
     * @param path 変更するファイルのパス
     * @exception SFTPException 変更に失敗した場合
     */
    public void chgrp(String gid, String path) throws SFTPException;
    
    /**
     * サーバ側の指定されたファイルのシンボリックリンクを作成する。<p>
     *
     * @param path 対象のファイルのパス
     * @param link 作成するシンボリックリンクのパス
     * @exception SFTPException 作成に失敗した場合
     */
    public void symlink(String path, String link) throws SFTPException;
    
    /**
     * サーバ側の指定されたファイルのハードリンクを作成する。<p>
     *
     * @param path 対象のファイルのパス
     * @param link 作成するハードリンクのパス
     * @exception SFTPException 作成に失敗した場合
     */
    public void ln(String path, String link) throws SFTPException;
    
    /**
     * サーバとの接続を切断する。<p>
     * 
     * @exception SFTPException 切断に失敗した場合
     */
    public void close() throws SFTPException;
    
    /**
     * SFTPファイル。<p>
     *
     * @author M.Takata
     */
    public interface SFTPFile{
        
        /**
         * ファイル名を取得する。<p>
         *
         * @return ファイル名
         */
        public String getName();
        
        /**
         * ユーザIDを取得する。<p>
         *
         * @return ユーザID
         */
        public int getUserId();
        
        /**
         * グループIDを取得する。<p>
         *
         * @return グループID
         */
        public int getGroupId();
        
        /**
         * 権限を取得する。<p>
         *
         * @return 権限
         */
        public int getPermissions();
        
        /**
         * 最終アクセス時刻を取得する。<p>
         *
         * @return 最終アクセス時刻
         */
        public Date getLastAccessTime();
        
        /**
         * 最終更新時刻を取得する。<p>
         *
         * @return 最終更新時刻
         */
        public Date getLastModificationTime();
        
        /**
         * ディレクトリかどうかを判定する。<p>
         *
         * @return ディレクトリの場合true
         */
        public boolean isDirectory();
        
        /**
         * リンクかどうかを判定する。<p>
         *
         * @return リンクの場合true
         */
        public boolean isLink();
        
        /**
         * ファイルサイズを取得する。<p>
         *
         * @return ファイルサイズ
         */
        public long size();
    }
}