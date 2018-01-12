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

import jp.ossc.nimbus.core.ServiceBaseMBean;
import jp.ossc.nimbus.core.ServiceName;

/**
 * {@link ClusterFTPClientFactoryService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see ClusterFTPClientFactoryService
 */
public interface ClusterFTPClientFactoryServiceMBean extends ServiceBaseMBean{
    
    /**
     * 複数のFTPサーバのうち、１台のみが稼働系となるモード。<p>
     * クラスタ化されたFTPサーバのうちの1台にだけ接続する。<br>
     * 接続の優先順位は、{@link #setFTPClientFactoryServiceNames(ServiceName[])}の順序に依存する。<br>
     */
    public static final int CLUSTER_MODE_ACTIVE_STANDBY = 1;
    
    /**
     * 複数のFTPサーバの全てが稼働系となるモード。<p>
     * クラスタ化されたFTPサーバの全てに接続する。<br>
     * 接続に失敗したFTPサーバが存在しても、少なくとも1台に接続できれば処理は続行する。<br>
     */
    public static final int CLUSTER_MODE_ACTIVE_ACTIVE  = 2;
    
    /**
     * デフォルトの接続失敗時のログメッセージID。<p>
     */
    public static final String MSG_ID_CONNECT_ERROR  = "CFTP_00001";
    
    /**
     * デフォルトの処理スキップ時のログメッセージID。<p>
     */
    public static final String MSG_ID_SKIP           = "CFTP_00002";
    
    /**
     * クラスタ化する{@link FTPClientFactory}サービスのサービス名配列を設定する。<p>
     *
     * @param names {@link FTPClientFactory}サービスのサービス名配列
     */
    public void setFTPClientFactoryServiceNames(ServiceName[] names);
    
    /**
     * クラスタ化する{@link FTPClientFactory}サービスのサービス名を取得する。<p>
     *
     * @return {@link FTPClientFactory}サービスのサービス名配列
     */
    public ServiceName[] getFTPClientFactoryServiceNames();
    
    /**
     * クラスタモードを設定する。<p>
     * デフォルトは、{@link #CLUSTER_MODE_ACTIVE_STANDBY}。<br>
     *
     * @param mode クラスタモード
     * @see #CLUSTER_MODE_ACTIVE_STANDBY
     * @see #CLUSTER_MODE_ACTIVE_ACTIVE
     */
    public void setClusterMode(int mode) throws IllegalArgumentException;
    
    /**
     * クラスタモードを取得する。<p>
     *
     * @return クラスタモード
     */
    public int getClusterMode();
    
    /**
     * 接続失敗時のログメッセージIDを設定する。<p>
     *
     * @param id ログメッセージID
     * @see #MSG_ID_CONNECT_ERROR
     */
    public void setConnectErrorMessageId(String id);
    
    /**
     * 接続失敗時のログメッセージIDを取得する。<p>
     *
     * @return ログメッセージID
     */
    public String getConnectErrorMessageId();
    
    /**
     * 処理スキップ時のログメッセージIDを設定する。<p>
     *
     * @param id ログメッセージID
     * @see #MSG_ID_CONNECT_ERROR
     */
    public void setSkipMessageId(String id);
    
    /**
     * 処理スキップ時のログメッセージIDを取得する。<p>
     *
     * @return ログメッセージID
     */
    public String getSkipMessageId();
}
