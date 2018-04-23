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
package jp.ossc.nimbus.service.test.action;

import java.io.File;
import java.util.Properties;

import jp.ossc.nimbus.core.ServiceBaseMBean;

/**
 * {@link SSHCommandExecuteActionService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see SSHCommandExecuteActionService
 */
public interface SSHCommandExecuteActionServiceMBean extends ServiceBaseMBean{
    
    /**
     * TCPの応答タイムアウト[ms]を設定する。<p>
     * デフォルトでは、タイムアウトしない。<br>
     *
     * @param timeout タイムアウト[ms]
     */
    public void setSessionTimeout(int timeout);
    
    /**
     * TCPの応答タイムアウト[ms]を取得する。<p>
     *
     * @return タイムアウト[ms]
     */
    public int getSessionTimeout();
    
    /**
     * KeepAliveメッセージの発信間隔[ms]を設定する。<p>
     *
     * @param interval 発信間隔[ms]
     */
    public void setServerAliveInterval(int interval);
    
    /**
     * KeepAliveメッセージの発信間隔[ms]を取得する。<p>
     *
     * @return 発信間隔[ms]
     */
    public int getServerAliveInterval();
    
    /**
     * KeepAliveメッセージの再送回数を設定する。<p>
     *
     * @param count 再送回数
     */
    public void setServerAliveCountMax(int count);
    
    /**
     * KeepAliveメッセージの再送回数を取得する。<p>
     *
     * @return 再送回数
     */
    public int getServerAliveCountMax();
    
    /**
     * 接続先サーバのホスト名を設定する。<p>
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
     * <a href="http://www.jcraft.com/jsch/">JSch - Java Secure Channel</a>のコンフィグを設定する。<p>
     *
     * @param conf コンフィグ
     */
    public void setConfig(Properties conf);
    
    /**
     * <a href="http://www.jcraft.com/jsch/">JSch - Java Secure Channel</a>のコンフィグを取得する。<p>
     *
     * @return コンフィグ
     */
    public Properties getConfig();
    
    /**
     * コマンドの終了待ちをする場合の、コマンド終了チェック間隔[ms]を設定する。<p>
     * デフォルトは、1秒。<br>
     *
     * @param interval チェック間隔[ms]
     */
    public void setCheckInterval(long interval);
    
    /**
     * コマンドの終了待ちをする場合の、コマンド終了チェック間隔[ms]を取得する。<p>
     *
     * @return チェック間隔[ms]
     */
    public long getCheckInterval();
    
    /**
     * コマンドを実行した結果の標準出力及びエラー出力の文字エンコーディングを設定する。<p>
     *
     * @param encoding 文字エンコーディング
     */
    public void setEncoding(String encoding);
    
    /**
     * コマンドを実行した結果の標準出力及びエラー出力の文字エンコーディングを取得する。<p>
     *
     * @return 文字エンコーディング
     */
    public String getEncoding();
    
    /**
     * このアクションのリソース定義を作成する際のデフォルトの想定コストを設定する。<p>
     * 
     * @param cost 想定コスト
     */
    public void setExpectedCost(double cost);
    
    /**
     * このアクションのリソース定義を作成する際のデフォルトの想定コストを取得する。<p>
     * 
     * @return 想定コスト
     */
    public double getExpectedCost();
}
