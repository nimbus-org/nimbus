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
package jp.ossc.nimbus.service.scp;

import java.io.File;

/**
 * SCPクライアント。<p>
 *
 * @author M.Takata
 */
public interface SCPClient{
    
    /**
     * サーバに接続する。<p>
     *
     * @param user ユーザ名
     * @param host 接続先サーバのホスト名
     * @param password パスワード
     * @exception SCPException サーバとの接続に失敗した場合
     */
    public void connect(String user, String host, String password) throws SCPException;
    
    /**
     * サーバに接続する。<p>
     *
     * @param user ユーザ名
     * @param host 接続先サーバのホスト名
     * @param port 接続先サーバのポート番号
     * @param password パスワード
     * @exception SCPException サーバとの接続に失敗した場合
     */
    public void connect(String user, String host, int port, String password) throws SCPException;
    
    /**
     * サーバに接続する。<p>
     *
     * @param user ユーザ名
     * @param host 接続先サーバのホスト名
     * @param pemFile 秘密鍵ファイル
     * @param passphrase パスフレーズ
     * @exception SCPException サーバとの接続に失敗した場合
     */
    public void connect(String user, String host, File pemFile, String passphrase) throws SCPException;
    
    /**
     * サーバに接続する。<p>
     *
     * @param user ユーザ名
     * @param host 接続先サーバのホスト名
     * @param port 接続先サーバのポート番号
     * @param pemFile 秘密鍵ファイル
     * @param passphrase パスフレーズ
     * @exception SCPException サーバとの接続に失敗した場合
     */
    public void connect(String user, String host, int port, File pemFile, String passphrase) throws SCPException;
    
    /**
     * サーバ側の指定されたファイルを取得する。<p>
     * 
     * @param remote 取得するファイルのパス
     * @return 取得したファイル
     * @exception SCPException 取得に失敗した場合
     */
    public File get(String remote) throws SCPException;
    
    /**
     * サーバ側の指定されたファイルを、指定された名前のファイルとして取得する。<p>
     * 
     * @param remote 取得するファイルのパス
     * @param local 取得後のファイル名
     * @return 取得したファイル
     * @exception SCPException 取得に失敗した場合
     */
    public File get(String remote, String local) throws SCPException;
    
    /**
     * サーバ側の指定された複数のファイルを取得する。<p>
     * 
     * @param remote 取得するファイルのパス
     * @return 取得したファイル
     * @exception SCPException 取得に失敗した場合
     */
    public File[] mget(String remote) throws SCPException;
    
    /**
     * サーバ側の指定された複数のファイルを取得する。<p>
     * 
     * @param remote 取得するファイルのパス
     * @param localDir 取得先のディレクトリ名
     * @return 取得したファイル
     * @exception SCPException 取得に失敗した場合
     */
    public File[] mget(String remote, String localDir) throws SCPException;
    
    /**
     * サーバ側に指定されたファイルを転送する。<p>
     * 
     * @param local 転送するファイルのパス
     * @exception SCPException 転送に失敗した場合
     */
    public void put(String local) throws SCPException;
    
    /**
     * サーバ側に指定されたファイルを、指定されたファイル名で転送する。<p>
     * 
     * @param local 転送するファイルのパス
     * @param remote 転送先でのファイル名
     * @exception SCPException 転送に失敗した場合
     */
    public void put(String local, String remote) throws SCPException;
    
    /**
     * サーバ側に指定されたファイルを、指定されたファイル名で転送する。<p>
     * 
     * @param local 転送するファイルのパス
     * @param remote 転送先でのファイル名
     * @param mode 転送先でのファイルの権限。数字４桁
     * @exception SCPException 転送に失敗した場合
     */
    public void put(String local, String remote, String mode) throws SCPException;
    
    /**
     * サーバ側に指定された全てのファイルを転送する。<p>
     * 
     * @param local 転送するファイルのパス
     * @exception SCPException 転送に失敗した場合
     */
    public void mput(String local) throws SCPException;
    
    /**
     * サーバ側に指定されたファイルを、指定されたファイル名で転送する。<p>
     * 
     * @param local 転送するファイルのパス
     * @param remoteDir 転送先のディレクトリ名
     * @exception SCPException 転送に失敗した場合
     */
    public void mput(String local, String remoteDir) throws SCPException;
    
    /**
     * サーバ側に指定されたファイルを、指定されたファイル名で転送する。<p>
     * 
     * @param local 転送するファイルのパス
     * @param remoteDir 転送先のディレクトリ名
     * @param mode 転送先でのファイルの権限。数字４桁
     * @exception SCPException 転送に失敗した場合
     */
    public void mput(String local, String remoteDir, String mode) throws SCPException;
    
    /**
     * サーバとの接続を切断する。<p>
     * 
     * @exception SCPException 切断に失敗した場合
     */
    public void close() throws SCPException;
}