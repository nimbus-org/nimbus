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
package jp.ossc.nimbus.service.http.proxy;

import jp.ossc.nimbus.core.*;

/**
 * {@link HttpProcessServiceBase}のMBeanインタフェース。<p>
 * 
 * @author M.Takata
 * @see HttpProcessServiceBase
 */
public interface HttpProcessServiceBaseMBean extends ServiceBaseMBean{
    
    /**
     * リクエストストリームの解凍を行うかどうかを設定する。<p>
     * デフォルトは、true。<br>
     *
     * @param isInflate 解凍を行う場合は、true
     */
    public void setRequestStreamInflate(boolean isInflate);
    
    /**
     * リクエストストリームの解凍を行うかどうかを判定する。<p>
     *
     * @return trueの場合、解凍を行う
     */
    public boolean isRequestStreamInflate();
    
    /**
     * HTTPトンネリングを行う場合にサーバとの接続に使用するSocketFactoryサービスのサービス名を設定する。<p>
     *
     * @param name SocketFactoryサービスのサービス名
     */
    public void setTunnelSocketFactoryServiceName(ServiceName name);
    
    /**
     * HTTPトンネリングを行う場合にサーバとの接続に使用するSocketFactoryサービスのサービス名を取得する。<p>
     *
     * @return SocketFactoryサービスのサービス名
     */
    public ServiceName getTunnelSocketFactoryServiceName();
    
    /**
     * HTTPトンネリングを行う場合の通信バッファサイズを設定する。<p>
     *
     * @param size 通信バッファサイズ[byte]
     */
    public void setTunnelBufferSize(int size);
    
    /**
     * HTTPトンネリングを行う場合の通信バッファサイズを取得する。<p>
     *
     * @return 通信バッファサイズ[byte]
     */
    public int getTunnelBufferSize();
    
    /**
     * プロキシのホスト名を取得する。<p>
     *
     * @return プロキシのホスト名
     */
    public String getProxyHost();
    
    /**
     * プロキシのホスト名を設定する。<p>
     *
     * @param host プロキシのホスト名
     */
    public void setProxyHost(String host);
    
    /**
     * プロキシのポート番号を取得する。<p>
     *
     * @return プロキシのポート番号
     */
    public int getProxyPort();
    
    /**
     * プロキシのポート番号を設定する。<p>
     *
     * @param port プロキシのポート番号
     */
    public void setProxyPort(int port);
    
    /**
     * プロキシのBASIC認証ユーザ名を取得する。<p>
     *
     * @return プロキシのBASIC認証ユーザ名
     */
    public String getProxyUser();
    
    /**
     * プロキシのBASIC認証ユーザ名を設定する。<p>
     *
     * @param user プロキシのBASIC認証ユーザ名
     */
    public void setProxyUser(String user);
    
    /**
     * プロキシのBASIC認証パスワードを取得する。<p>
     *
     * @return プロキシのBASIC認証パスワード
     */
    public String getProxyPassword();
    
    /**
     * プロキシのBASIC認証パスワードを設定する。<p>
     *
     * @param password プロキシのBASIC認証パスワード
     */
    public void setProxyPassword(String password);
    
    /**
     * トンネリング通信をサポートするかどうかを判定する。<p>
     *
     * @return trueの場合、トンネリング通信をサポートする
     */
    public boolean isSupportTunnelling();
    
    /**
     * トンネリング通信をサポートするかどうかを設定する。<p>
     * デフォルトは、falseで、サポートしない。
     *
     * @param isSupport トンネリング通信をサポートする場合true
     */
    public void setSupportTunnelling(boolean isSupport);
}