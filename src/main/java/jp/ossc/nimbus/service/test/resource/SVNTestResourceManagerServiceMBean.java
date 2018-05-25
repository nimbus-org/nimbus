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
package jp.ossc.nimbus.service.test.resource;

import java.io.File;

/**
 * {@link SVNTestResourceManagerService}のMBeanインタフェース
 * <p>
 *
 * @author M.Ishida
 * @see SVNTestResourceManagerService
 */
public interface SVNTestResourceManagerServiceMBean {

    /**
     * SVNサーバへ接続する際のプロトコルgit用定数。
     * <p>
     */
    public static String PROTOCOL_SVN = "svn";

    /**
     * SVNサーバへ接続する際のプロトコルhttp用定数。
     * <p>
     */
    public static String PROTOCOL_HTTP = "http";

    /**
     * SVNサーバへ接続する際のプロトコルhttps用定数。
     * <p>
     */
    public static String PROTOCOL_HTTPS = "https";

    /**
     * SVNサーバへ接続する際のプロトコルsvn+ssh用定数。
     * <p>
     */
    public static String PROTOCOL_SVN_SSH = "svn+ssh";

    /**
     * SVNサーバへ接続する際のプロトコルfile用定数。
     * <p>
     */
    public static String PROTOCOL_FILE = "file";


    /**
     * SVNサーバへ接続する際のプロトコルを取得する。
     * <p>
     *
     * @return プロトコル
     */
    public String getProtocol();

    /**
     * SVNサーバへ接続する際のプロトコルを設定する。
     * <p>
     *
     * @param protocol プロトコル
     */
    public void setProtocol(String protocol);


    /**
     * SVNサーバへ接続する際のユーザ名を取得する。
     * <p>
     *
     * @return ユーザ名
     */
    public String getUserName();

    /**
     * SVNサーバへ接続する際のユーザ名を設定する。
     * <p>
     *
     * @param user ユーザ名
     */
    public void setUserName(String user);

    /**
     * SVNサーバへ接続する際のパスワードを取得する。
     * <p>
     *
     * @return パスワード
     */
    public String getPassword();

    /**
     * SVNサーバへ接続する際のパスワードを設定する。
     * <p>
     *
     * @param str パスワード
     */
    public void setPassword(String str);

    /**
     * SVNサーバへ接続する際のサーバ名を取得する。
     * <p>
     *
     * @return サーバ名
     */
    public String getServerName();

    /**
     * SVNサーバへ接続する際のサーバ名を設定する。
     * <p>
     *
     * @param server サーバ名
     */
    public void setServerName(String server);

    /**
     * SVNサーバへ接続する際のポートを取得する。
     * <p>
     *
     * @return ポート
     */
    public int getPort();

    /**
     * SVNサーバへ接続する際のポートを設定する。
     * <p>
     *
     * @param port ポート
     */
    public void setPort(int port);

    /**
     * SVNサーバへ接続する際のリポジトリパスを取得する。
     * <p>
     *
     * @return リポジトリパス
     */
    public String getRepositoryPath();

    /**
     * SVNサーバへ接続する際のリポジトリパスを設定する。
     * <p>
     *
     * @param path リポジトリパス
     */
    public void setRepositoryPath(String path);

    /**
     * SVNサーバへ接続する際のモジュールパスを取得する。
     * <p>
     *
     * @return モジュールパス
     */
    public String getModulePath();

    /**
     * SVNサーバへ接続する際のモジュールパスを設定する。
     * <p>
     *
     * @param module モジュールパス
     */
    public void setModulePath(String module);

    /**
     * SVNサーバからモジュールをチェックアウトするディレクトリを取得する。
     * <p>
     *
     * @return モジュールをチェックアウトするディレクトリ
     */
    public File getSvnCheckOutDirectory();

    /**
     * SVNサーバからモジュールをチェックアウトするディレクトリを設定する。
     * <p>
     *
     * @param directory モジュールをチェックアウトするディレクトリ
     */
    public void setSvnCheckOutDirectory(File directory);

    /**
     * SVNサーバへ接続する際のブランチ名を取得する。
     * <p>
     *
     * @return ブランチ名
     */
    public String getTargetBranch();

    /**
     * SVNサーバへ接続する際のブランチ名を設定する。
     * <p>
     *
     * @param branch ブランチ名
     */
    public void setTargetBranch(String branch);

    /**
     * SVNサーバへ接続する際のタグ名を取得する。
     * <p>
     *
     * @return タグ名
     */
    public String getTargetTag();

    /**
     * SVNサーバへ接続する際のタグ名を設定する。
     * <p>
     *
     * @param tag タグ名
     */
    public void setTargetTag(String tag);

    /**
     * SVNコマンド実行時のDebugログ有効/無効を返却する。
     *
     * @return Debugログ有効/無効
     */
    public boolean isDebugEnabled();

    /**
     * SVNコマンド実行時のDebugログ有効/無効を設定する。
     *
     * @param enabled Debugログ有効/無効
     */
    public void setDebugEnabled(boolean enabled);

    /**
     * SVNコマンド実行時のInfoログ有効/無効を返却する。
     *
     * @return Infoログ有効/無効
     */
    public boolean isInfoEnabled();

    /**
     * SVNコマンド実行時のInfoログ有効/無効を設定する。
     *
     * @param enabled Infoログ有効/無効
     */
    public void setInfoEnabled(boolean enabled);

    /**
     * SVNコマンド実行時のWarnログ有効/無効を返却する。
     *
     * @return Warnログ有効/無効
     */
    public boolean isWarnEnabled();

    /**
     * SVNコマンド実行時のWarnログ有効/無効を設定する。
     *
     * @param enabled Warnログ有効/無効
     */
    public void setWarnEnabled(boolean enabled);

    /**
     * SVNコマンド実行時のErrorログ有効/無効を返却する。
     *
     * @return Errorログ有効/無効
     */
    public boolean isErrorEnabled();

    /**
     * SVNコマンド実行時のErrorログ有効/無効を設定する。
     *
     * @param enabled Errorログ有効/無効
     */
    public void setErrorEnabled(boolean enabled);
    
    /**
     * SVNサーバからチェックアウトする際に使用する一時ディレクトリを取得する。
     * 
     * @return 一時ディレクトリ
     */
    public File getTemporaryDirectory();
    
    /**
     * SVNサーバからチェックアウトする際に使用する一時ディレクトリを設定する。
     * 
     * @param path 一時ディレクトリ
     */
    public void setTemporaryDirectory(File path);
}
