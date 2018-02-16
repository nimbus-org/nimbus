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
package jp.ossc.nimbus.service.ftp;

import java.io.File;

/**
 * FTPクライアント。<p>
 *
 * @author M.Takata
 */
public interface FTPClient{
    
    /**
     * サーバに接続する。<p>
     *
     * @param host 接続先サーバのホスト名
     * @exception FTPException サーバとの接続に失敗した場合
     */
    public void connect(String host) throws FTPException;
    
    /**
     * サーバに接続する。<p>
     *
     * @param host 接続先サーバのホスト名
     * @param port 接続先サーバのポート番号
     * @exception FTPException サーバとの接続に失敗した場合
     */
    public void connect(String host, int port) throws FTPException;
    
    /**
     * サーバに接続する。<p>
     *
     * @param host 接続先サーバのホスト名
     * @param port 接続先サーバのポート番号
     * @param localAddr クライアントのアドレス
     * @param localPort クライアントのポート番号
     * @exception FTPException サーバとの接続に失敗した場合
     */
    public void connect(
        String host,
        int port,
        String localAddr,
        int localPort
    ) throws FTPException;
    
    /**
     * ログインする。<p>
     *
     * @param user ユーザ名
     * @param password パスワード
     * @exception FTPException ログインに失敗した場合
     */
    public void login(String user, String password) throws FTPException;
    
    /**
     * ログアウトする。<p>
     * 
     * @exception FTPException ログアウトに失敗した場合
     */
    public void logout() throws FTPException;
    
    /**
     * サーバのファイル名一覧を取得する。<p>
     * 
     * @return ファイル名の配列
     * @exception FTPException 取得に失敗した場合
     */
    public String[] ls() throws FTPException;
    
    /**
     * サーバの指定されたディレクトリ内のファイル名一覧を取得する。<p>
     * 
     * @return ファイル名の配列
     * @exception FTPException 取得に失敗した場合
     */
    public String[] ls(String path) throws FTPException;
    
    /**
     * サーバ側でのカレントディレクトリを取得する。<p>
     * 
     * @return カレントディレクトリのパス
     * @exception FTPException 取得に失敗した場合
     */
    public String pwd() throws FTPException;
    
    /**
     * クライアント側でのカレントディレクトリを取得する。<p>
     * 
     * @return カレントディレクトリ
     * @exception FTPException 取得に失敗した場合
     */
    public File lpwd() throws FTPException;
    
    /**
     * サーバ側でのカレントディレクトリを指定されたパスに移動する。<p>
     * 
     * @param path 移動先のパス
     * @exception FTPException 移動に失敗した場合
     */
    public void cd(String path) throws FTPException;
    
    /**
     * クライアント側でのカレントディレクトリを指定されたパスに移動する。<p>
     * 
     * @param path 移動先のパス
     * @exception FTPException 移動に失敗した場合
     */
    public void lcd(String path) throws FTPException;
    
    /**
     * サーバ側で指定されたディレクトリを作成する。<p>
     * 
     * @param dir 作成するディレクトリのパス
     * @exception FTPException 作成に失敗した場合
     */
    public void mkdir(String dir) throws FTPException;
    
    /**
     * サーバ側で指定されたファイルのファイル名を変更する。<p>
     * 
     * @param from 変更対象のファイルのパス
     * @param to 変更後のファイル名
     * @exception FTPException 変更に失敗した場合
     */
    public void rename(String from, String to) throws FTPException;
    
    /**
     * サーバ側の指定されたファイルを取得する。<p>
     * 
     * @param path 取得するファイルのパス
     * @return 取得したファイル
     * @exception FTPException 取得に失敗した場合
     */
    public File get(String path) throws FTPException;
    
    /**
     * サーバ側の指定されたファイルを、指定された名前のファイルとして取得する。<p>
     * 
     * @param remote 取得するファイルのパス
     * @param local 取得後のファイル名
     * @return 取得したファイル
     * @exception FTPException 取得に失敗した場合
     */
    public File get(String remote, String local) throws FTPException;
    
    /**
     * サーバ側の指定された全てのファイルを取得する。<p>
     * 
     * @param path 取得するファイルのパス
     * @return 取得したファイル配列
     * @exception FTPException 取得に失敗した場合
     */
    public File[] mget(String path) throws FTPException;
    
    /**
     * サーバ側に指定されたファイルを転送する。<p>
     * 
     * @param path 転送するファイルのパス
     * @exception FTPException 転送に失敗した場合
     */
    public void put(String path) throws FTPException;
    
    /**
     * サーバ側に指定されたファイルを、指定されたファイル名で転送する。<p>
     * 
     * @param local 転送するファイルのパス
     * @param remote 転送先でのファイル名
     * @exception FTPException 転送に失敗した場合
     */
    public void put(String local, String remote) throws FTPException;
    
    /**
     * サーバ側に指定された全てのファイルを転送する。<p>
     * 
     * @param path 転送するファイルのパス
     * @exception FTPException 転送に失敗した場合
     */
    public void mput(String path) throws FTPException;
    
    /**
     * サーバ側の指定されたファイルを削除する。<p>
     * 
     * @param path 削除するファイルのパス
     * @exception FTPException 削除に失敗した場合
     */
    public void delete(String path) throws FTPException;
    
    /**
     * サーバ側の指定された全てのファイルを削除する。<p>
     * 
     * @param path 削除するファイルのパス
     * @exception FTPException 削除に失敗した場合
     */
    public void mdelete(String path) throws FTPException;
    
    /**
     * 転送モードをASCIIに変更する。<p>
     * 
     * @exception FTPException 変更に失敗した場合
     */
    public void ascii() throws FTPException;
    
    /**
     * 転送モードをバイナリに変更する。<p>
     * 
     * @exception FTPException 変更に失敗した場合
     */
    public void binary() throws FTPException;
    
    /**
     * 転送モードを指定されたモードに変更する。<p>
     * ここで指定する転送モードは、実装依存。<br>
     *
     * @param type 転送モード
     * @exception FTPException 変更に失敗した場合
     */
    public void setTransferType(int type) throws FTPException;
    
    /**
     * 現在の転送モードを取得する。<p>
     *
     * @return 転送モード
     * @exception FTPException 取得に失敗した場合
     */
    public int getTransferType() throws FTPException;
    
    /**
     * アクティブFTPに切り替える。<p>
     * 
     * @exception FTPException 変更に失敗した場合
     */
    public void active() throws FTPException;
    
    /**
     * パッシブFTPに切り替える。<p>
     * 
     * @exception FTPException 変更に失敗した場合
     */
    public void passive() throws FTPException;
    
    /**
     * サーバとの接続を切断する。<p>
     * 
     * @exception FTPException 切断に失敗した場合
     */
    public void close() throws FTPException;
}