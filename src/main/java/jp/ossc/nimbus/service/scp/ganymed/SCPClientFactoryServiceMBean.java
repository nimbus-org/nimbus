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
package jp.ossc.nimbus.service.scp.ganymed;

import java.io.File;

import jp.ossc.nimbus.core.*;

/**
 * {@link SCPClientFactoryService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see SCPClientFactoryService
 */
public interface SCPClientFactoryServiceMBean extends ServiceBaseMBean{
    
    /**
     * TCPの接続タイムアウト[ms]を設定する。<p>
     * デフォルトでは、タイムアウトしない。<br>
     *
     * @param timeout タイムアウト[ms]
     */
    public void setConnectionTimeout(int timeout);
    
    /**
     * TCPの接続タイムアウト[ms]を取得する。<p>
     *
     * @return タイムアウト[ms]
     */
    public int getConnectionTimeout();
    
    /**
     * SSH接続タイムアウト[ms]を設定する。<p>
     * デフォルトでは、タイムアウトしない。<br>
     *
     * @param timeout タイムアウト[ms]
     */
    public void setKeyExchangeTimeout(int timeout);
    
    /**
     * SSH接続タイムアウト[ms]を取得する。<p>
     *
     * @return タイムアウト[ms]
     */
    public int getKeyExchangeTimeout();
    
    /**
     * TCP_NODELAYの有効/無効を設定する。<p>
     *
     * @param noDelay 有効にする場合true
     */
    public void setTcpNoDelay(boolean noDelay);
    
    /**
     * TCP_NODELAYの有効/無効を判定する。<p>
     *
     * @return trueの場合、有効
     */
    public boolean isTcpNoDelay();
    
    /**
     * 接続先サーバのホスト名を設定する。<p>
     * この属性を指定した場合、{@link SCPClientFactoryService#createSCPClient()}で生成した{@link jp.ossc.nimbus.service.scp.SCPClient SCPClient}は、接続済となる。<br>
     *
     * @param addr ホスト名
     */
    public void setHostName(String addr);
    
    /**
     * 接続先サーバのホスト名を取得する。<p>
     *
     * @return ホスト名
     */
    public String getHostName();
    
    /**
     * 接続先サーバのポート番号を設定する。<p>
     *
     * @param port ポート番号
     */
    public void setPort(int port);
    
    /**
     * 接続先サーバのポート番号を取得する。<p>
     *
     * @return ポート番号
     */
    public int getPort();
    
    /**
     * 認証するユーザ名を設定する。<p>
     * この属性を指定した場合、{@link SCPClientFactoryService#createSCPClient()}で生成した{@link jp.ossc.nimbus.service.scp.SCPClient SCPClient}は、認証済となる。<br>
     *
     * @param name ユーザ名
     */
    public void setUserName(String name);
    
    /**
     * 認証するユーザ名を取得する。<p>
     *
     * @return ユーザ名
     */
    public String getUserName();
    
    /**
     * 認証するユーザのパスワードまたは秘密鍵のパスフレーズを設定する。<p>
     *
     * @param password パスワードまたは秘密鍵のパスフレーズ
     */
    public void setPassword(String password);
    
    /**
     * 認証するユーザのパスワードまたは秘密鍵のパスフレーズを取得する。<p>
     *
     * @return パスワードまたは秘密鍵のパスフレーズ
     */
    public String getPassword();
    
    /**
     * 秘密鍵ファイルのパスを設定する。<p>
     *
     * @param path 秘密鍵ファイルのパス
     */
    public void setPemFile(File path);
    
    /**
     * 秘密鍵ファイルのパスを取得する。<p>
     *
     * @return 秘密鍵ファイルのパス
     */
    public File getPemFile();
    
    /**
     * 鍵交換アルゴリズムを設定する。<p>
     *
     * @param algos 鍵交換アルゴリズム名配列
     */
    public void setServerHostKeyAlgorithms(String[] algos);
    
    /**
     * 鍵交換アルゴリズムを取得する。<p>
     *
     * @return 鍵交換アルゴリズム名配列
     */
    public String[] getServerHostKeyAlgorithms();
    
    /**
     * ログイン直後のクライアントのホームディレクトリを設定する。<p>
     * 設定しない場合は、システムプロパティ"user.home"の示すディレクトリとなる。<br>
     * 
     * @param dir ホームディレクトリ
     */
    public void setHomeDirectory(File dir);
    
    /**
     * ログイン直後のクライアントのホームディレクトリを取得する。<p>
     * 
     * @return ホームディレクトリ
     */
    public File getHomeDirectory();
    
    /**
     * ローカルとリモートが異なるファイルセパレータを使用する場合に、リモートのファイルセパレータを設定する。<p>
     * ローカルとリモートが同じファイルセパレータを使用する場合は、設定する必要はない。<br>
     *
     * @param separator リモートのファイルセパレータ
     */
    public void setRemoteFileSeparator(String separator);
    
    /**
     * ローカルとリモートが異なるファイルセパレータを使用する場合に、リモートのファイルセパレータを取得する。<p>
     *
     * @return リモートのファイルセパレータ
     */
    public String getRemoteFileSeparator();
}