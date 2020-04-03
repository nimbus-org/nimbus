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
package jp.ossc.nimbus.service.scheduler2.k8s;

import java.util.Set;

import jp.ossc.nimbus.service.scheduler2.AbstractScheduleExecutorServiceMBean;

/**
 * {@link KuberneteseScheduleExecutorService}のMBeanインタフェース。<p>
 * 
 * @author M.Takata
 */
public interface KuberneteseScheduleExecutorServiceMBean extends AbstractScheduleExecutorServiceMBean{
    
    /**
     * デフォルトのスケジュール実行種別。<p>
     */
    public static final String DEFAULT_EXECUTOR_TYPE = "K8S";
    
    /**
     * 使用するAPIのクラスを設定する。<p>
     *
     * @param classes 使用するAPIのクラス配列
     */
    public void setApiClasses(Class[] classes);
    
    /**
     * 使用するAPIのクラスを取得する。<p>
     *
     * @return 使用するAPIのクラス配列
     */
    public Class[] getApiClasses();
    
    /**
     * kube-apiserverのURLを設定する。<p>
     *
     * @param url kube-apiserverのURL
     */
    public void setURL(String url);
    
    /**
     * kube-apiserverのURLを取得する。<p>
     *
     * @return kube-apiserverのURL
     */
    public String getURL();
    
    /**
     * kube-apiserverとの通信でTLS/SSLを有効にするかどうかを設定する。<p>
     * デフォルトは、trueで有効。<br>
     *
     * @param isValidate 有効にする場合は、true
     */
    public void setValidateSSL(boolean isValidate);
    
    /**
     * kube-apiserverとの通信でTLS/SSLを有効にするかどうかを判定する。<p>
     *
     * @return trueの場合、有効
     */
    public boolean isValidateSSL();
    
    /**
     * kube-apiserverとの認証に使用するユーザを設定する。<p>
     *
     * @param user 認証ユーザ
     */
    public void setUser(String user);
    
    /**
     * kube-apiserverとの認証に使用するユーザを取得する。<p>
     *
     * @return 認証ユーザ
     */
    public String getUser();
    
    /**
     * kube-apiserverとの認証に使用するパスワードを設定する。<p>
     *
     * @param password パスワード
     */
    public void setPassword(String password);
    
    /**
     * kube-apiserverとの認証に使用するパスワードを取得する。<p>
     *
     * @return パスワード
     */
    public String getPassword();
    
    /**
     * kube-apiserverとの認証に使用する認証トークンを設定する。<p>
     *
     * @param token 認証トークン
     */
    public void setToken(String token);
    
    /**
     * kube-apiserverとの認証に使用する認証トークンを取得する。<p>
     *
     * @return 認証トークン
     */
    public String getToken();
    
    /**
     * kube-apiserverとの認証に使用する設定ファイルのパスを設定する。<p>
     *
     * @param path 設定ファイルのパス
     */
    public void setConfigFilePath(String path);
    
    /**
     * kube-apiserverとの認証に使用する設定ファイルのパスを取得する。<p>
     *
     * @return 設定ファイルのパス
     */
    public String getConfigFilePath();
    
    /**
     * kube-apiserverとの認証に使用する設定ファイルの文字コードを設定する。<p>
     * デフォルトは、OSの文字コード。<br>
     *
     * @param encode 設定ファイルの文字コード
     */
    public void setConfigFileEncoding(String encode);
    
    /**
     * kube-apiserverとの認証に使用する設定ファイルの文字コードを取得する。<p>
     *
     * @return 設定ファイルの文字コード
     */
    public String getConfigFileEncoding();
    
    /**
     * Podを監視する際のHTTP通信の接続タイムアウトを設定する。<p>
     * デフォルトは、3000[ms]。<br>
     *
     * @param millis タイムアウト[ms]
     */
    public void setWriteTimeout(int millis);
    
    /**
     * Podを監視する際のHTTP通信の接続タイムアウトを取得する。<p>
     *
     * @return タイムアウト[ms]
     */
    public int getWriteTimeout();
    
    /**
     * Podを監視する際のHTTP通信の応答タイムアウトを設定する。<p>
     * デフォルトは、3000[ms]。<br>
     *
     * @param millis タイムアウト[ms]
     */
    public void setReadTimeout(int millis);
    
    /**
     * Podを監視する際のHTTP通信の応答タイムアウトを取得する。<p>
     *
     * @return タイムアウト[ms]
     */
    public int getReadTimeout();
    
    /**
     * APIクラスのメソッドで、タスクとして使用しないメソッド名を設定する。<p>
     * Object.classのメソッドと、setApiClientメソッドは、自動で含まれる。<br>
     *
     * @param methodNames メソッド名の集合
     */
    public void setNotApiMethodNames(Set methodNames);
    
    /**
     * APIクラスのメソッドで、タスクとして使用しないメソッド名を取得する。<p>
     *
     * @return メソッド名の集合
     */
    public Set getNotApiMethodNames();
}
