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
 * {@link CVSTestResourceManagerService}のMBeanインタフェース
 * <p>
 *
 * @author M.Ishida
 * @see CVSTestResourceManagerService
 */
public interface GitTestResourceManagerServiceMBean {

    /**
     * Gitサーバへ接続する際のプロトコルgit用定数。
     * <p>
     */
    public static String PROTOCOL_GIT = "git";

    /**
     * Gitサーバへ接続する際のプロトコルhttp用定数。
     * <p>
     */
    public static String PROTOCOL_HTTP = "http";

    /**
     * Gitサーバへ接続する際のプロトコルhttps用定数。
     * <p>
     */
    public static String PROTOCOL_HTTPS = "https";

    /**
     * Gitサーバへ接続する際のプロトコルssh用定数。
     * <p>
     */
    public static String PROTOCOL_SSH = "ssh";

    /**
     * Gitサーバへ接続する際のプロトコルfile用定数。
     * <p>
     */
    public static String PROTOCOL_FILE = "file";

    /**
     * Gitサーバへ接続する際のプロトコルを取得する。
     * <p>
     *
     * @return メソッド
     */
    public String getProtocol();

    /**
     * Gitサーバへ接続する際のプロトコルを設定する。
     * <p>
     *
     * @param protocol メソッド
     */
    public void setProtocol(String protocol);

    /**
     * Gitサーバへ接続する際のユーザ名を取得する。
     * <p>
     *
     * @return ユーザ名
     */
    public String getUserName();

    /**
     * Gitサーバへ接続する際のユーザ名を設定する。
     * <p>
     *
     * @param user ユーザ名
     */
    public void setUserName(String user);

    /**
     * Gitサーバへ接続する際のパスワードを取得する。
     * <p>
     *
     * @return パスワード
     */
    public String getPassword();

    /**
     * Gitサーバへ接続する際のパスワードを設定する。
     * <p>
     *
     * @param str パスワード
     */
    public void setPassword(String str);

    /**
     * Gitサーバへ接続する際のサーバ名を取得する。
     * <p>
     *
     * @return サーバ名
     */
    public String getServerName();

    /**
     * Gitサーバへ接続する際のサーバ名を設定する。
     * <p>
     *
     * @param server サーバ名
     */
    public void setServerName(String server);

    /**
     * Gitサーバへ接続する際のポートを取得する。
     * <p>
     *
     * @return ポート
     */
    public int getPort();

    /**
     * Gitサーバへ接続する際のポートを設定する。
     * <p>
     *
     * @param port ポート
     */
    public void setPort(int port);

    /**
     * Gitサーバへ接続する際のリポジトリパスを取得する。
     * <p>
     *
     * @return リポジトリパス
     */
    public String getRepositoryPath();

    /**
     * Gitサーバへ接続する際のリポジトリパスを設定する。
     * <p>
     *
     * @param path リポジトリパス
     */
    public void setRepositoryPath(String path);

    /**
     * Gitサーバからチェックアウト後に取得するモジュールパスを取得する。
     * <p>
     *
     * @return モジュールパス
     */
    public String getModulePath();

    /**
     * Gitサーバからチェックアウト後に取得するモジュールパスを設定する。
     * <p>
     *
     * @param module モジュールパス
     */
    public void setModulePath(String module);

    /**
     * Gitサーバからモジュールをチェックアウトするディレクトリを取得する。
     * <p>
     *
     * @return モジュールをチェックアウトするディレクトリ
     */
    public File getGitCheckOutDirectory();

    /**
     * Gitサーバからモジュールをチェックアウトするディレクトリを設定する。
     * <p>
     *
     * @param directory モジュールをチェックアウトするディレクトリ
     */
    public void setGitCheckOutDirectory(File directory);

    /**
     * Gitサーバへ接続する際のブランチ名を取得する。
     * <p>
     *
     * @return ブランチ名
     */
    public String getTargetBranch();

    /**
     * Gitサーバへ接続する際のブランチ名を設定する。
     * <p>
     *
     * @param branch ブランチ名
     */
    public void setTargetBranch(String branch);

    /**
     * Gitサーバへ接続する際のタグ名を取得する。
     * <p>
     *
     * @return タグ名
     */
    public String getTargetTag();

    /**
     * Gitサーバへ接続する際のタグ名を設定する。
     * <p>
     *
     * @param tag タグ名
     */
    public void setTargetTag(String tag);

    /**
     * Gitコマンド実行時のDebugログ有効/無効を返却する。
     *
     * @return Debugログ有効/無効
     */
    public boolean isDebugEnabled();

    /**
     * Gitコマンド実行時のDebugログ有効/無効を設定する。
     *
     * @param enabled Debugログ有効/無効
     */
    public void setDebugEnabled(boolean enabled);

    /**
     * Gitコマンド実行時のInfoログ有効/無効を返却する。
     *
     * @return Infoログ有効/無効
     */
    public boolean isInfoEnabled();

    /**
     * Gitコマンド実行時のInfoログ有効/無効を設定する。
     *
     * @param enabled Infoログ有効/無効
     */
    public void setInfoEnabled(boolean enabled);

    /**
     * Gitコマンド実行時のWarnログ有効/無効を返却する。
     *
     * @return Warnログ有効/無効
     */
    public boolean isWarnEnabled();

    /**
     * Gitコマンド実行時のWarnログ有効/無効を設定する。
     *
     * @param enabled Warnログ有効/無効
     */
    public void setWarnEnabled(boolean enabled);

    /**
     * Gitコマンド実行時のErrorログ有効/無効を返却する。
     *
     * @return Errorログ有効/無効
     */
    public boolean isErrorEnabled();

    /**
     * Gitコマンド実行時のErrorログ有効/無効を設定する。
     *
     * @param enabled Errorログ有効/無効
     */
    public void setErrorEnabled(boolean enabled);

    /**
     * Gitサーバからチェックアウトする際に使用する一時ディレクトリを取得する。
     * 
     * @return 一時ディレクトリ
     */
    public File getTemporaryDirectory();
    
    /**
     * Gitサーバからチェックアウトする際に使用する一時ディレクトリを設定する。
     * 
     * @param path 一時ディレクトリ
     */
    public void setTemporaryDirectory(File path);

}
