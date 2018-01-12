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
package jp.ossc.nimbus.service.ftp.ftpclient;

import java.util.Map;
import java.io.File;

import jp.ossc.nimbus.core.*;

/**
 * {@link FTPClientFactoryService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see FTPClientFactoryService
 */
public interface FTPClientFactoryServiceMBean extends ServiceBaseMBean{
    
    /**
     * SOタイムタウトを設定する。<p>
     *
     * @param timeout タイムタウト[ms]
     */
    public void setSoTimeout(int timeout);
    
    /**
     * SOタイムタウトを取得する。<p>
     *
     * @return タイムタウト[ms]
     */
    public int getSoTimeout();
    
    /**
     * SOリンガーの遅延時間を設定する。<p>
     *
     * @param time 遅延時間[ms]
     */
    public void setSoLinger(int time);
    
    /**
     * SOリンガーの遅延時間を取得する。<p>
     *
     * @return 遅延時間[ms]
     */
    public int getSoLinger();
    
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
     * この属性を指定した場合、{@link FTPClientFactoryService#createFTPClient()}で生成した{@link jp.ossc.nimbus.service.ftp.FTPClient FTPClient}は、接続済となる。<br>
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
     * クライアントのアドレスを設定する。<p>
     *
     * @param addr アドレス
     */
    public void setBindAddress(String addr);
    
    /**
     * クライアントのアドレスを取得する。<p>
     *
     * @return アドレス
     */
    public String getBindAddress();
    
    /**
     * クライアントのポート番号を設定する。<p>
     *
     * @param port ポート番号
     */
    public void setLocalPort(int port);
    
    /**
     * クライアントのポート番号を取得する。<p>
     *
     * @return ポート番号
     */
    public int getLocalPort();
    
    /**
     * ログインするユーザ名を設定する。<p>
     * この属性を指定した場合、{@link FTPClientFactoryService#createFTPClient()}で生成した{@link jp.ossc.nimbus.service.ftp.FTPClient FTPClient}は、ログイン済となる。<br>
     *
     * @param name ユーザ名
     */
    public void setUserName(String name);
    
    /**
     * ログインするユーザ名を取得する。<p>
     *
     * @return ユーザ名
     */
    public String getUserName();
    
    /**
     * ログインするユーザのパスワードを設定する。<p>
     *
     * @param password パスワード
     */
    public void setPassword(String password);
    
    /**
     * ログインするユーザのパスワードを取得する。<p>
     *
     * @return パスワード
     */
    public String getPassword();
    
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
     * Javaの正規表現を使用するかどうかを設定する。<p>
     * trueに設定した場合、{@link jp.ossc.nimbus.service.ftp.FTPClient#mput(String) mput(String)}、{@link jp.ossc.nimbus.service.ftp.FTPClient#mget(String) mget(String)}、{@link jp.ossc.nimbus.service.ftp.FTPClient#mdelete(String) mdelete(String)}におけるファイル名の指定で、Javaの正規表現を使用できる。<br>
     * デフォルトは、falseで、ワイルドカード指定のみ有効。<br>
     * 
     * @param isEnabled 使用する場合は、true
     */
    public void setJavaRegexEnabled(boolean isEnabled);
    
    /**
     * Javaの正規表現を使用するかどうかを判定する。<p>
     * 
     * @return trueの場合、使用する
     */
    public boolean isJavaRegexEnabled();
    
    /**
     * パッシブFTPするかどうかを設定する。<p>
     *
     * @param isPassive パッシブFTPにする場合true
     */
    public void setPassive(boolean isPassive);
    
    /**
     * パッシブFTPするかどうかを判定する。<p>
     *
     * @return trueの場合、パッシブFTP
     */
    public boolean isPassive();
    
    /**
     * 接続時の最大リトライ回数を設定する。<p>
     * デフォルトは、0でリトライしない。<br>
     *
     * @param count 最大リトライ回数
     */
    public void setConnectMaxRetryCount(int count);
    
    /**
     * 接続時の最大リトライ回数を取得する。<p>
     *
     * @return 最大リトライ回数
     */
    public int getConnectMaxRetryCount();
    
    /**
     * {@link jp.ossc.nimbus.service.ftp.FTPClient FTPClient}に設定するプロパティを設定する。<p>
     *
     * @param name プロパティ名
     * @param value 値
     */
    public void setFTPClientProperty(String name, Object value);
    
    /**
     * {@link jp.ossc.nimbus.service.ftp.FTPClient FTPClient}の指定されたプロパティ名のプロパティ値を取得する。<p>
     *
     * @param name プロパティ名
     * @return 値
     */
    public Object getFTPClientProperty(String name);
    
    /**
     * {@link jp.ossc.nimbus.service.ftp.FTPClient FTPClient}の指定されたプロパティ名のプロパティ値を削除する。<p>
     *
     * @param name プロパティ名
     */
    public void removeFTPClientProperty(String name);
    
    /**
     * {@link jp.ossc.nimbus.service.ftp.FTPClient FTPClient}に設定するプロパティをクリアする。<p>
     */
    public void clearFTPClientProperties();
    
    /**
     * {@link jp.ossc.nimbus.service.ftp.FTPClient FTPClient}に設定するプロパティを取得する。<p>
     *
     * @return FTPClientに設定するプロパティ集合
     */
    public Map getFTPClientProperties();
}