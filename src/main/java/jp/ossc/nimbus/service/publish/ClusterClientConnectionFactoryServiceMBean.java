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
package jp.ossc.nimbus.service.publish;

import jp.ossc.nimbus.core.ServiceBaseMBean;
import jp.ossc.nimbus.core.ServiceName;

/**
 * {@link ClusterClientConnectionFactoryService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see ClusterClientConnectionFactoryService
 */
public interface ClusterClientConnectionFactoryServiceMBean extends ServiceBaseMBean{
    
    public static final String MSG_ID_CONNECT_ERROR = "PCCF_00001";
    
    /**
     * {@link jp.ossc.nimbus.service.keepalive.Cluster Cluster}サービスのサービス名を設定する。<p>
     * クラスタのメンバー情報から、{@link ClientConnectionFactory}リモートオブジェクトを取得する事で、クラスタに参加しているClientConnectionFactoryをクライアント側で共有する。<br>
     *
     * @param name Clusterサービスのサービス名
     */
    public void setClusterServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.keepalive.Cluster Cluster}サービスのサービス名を取得する。<p>
     *
     * @return Clusterサービスのサービス名
     */
    public ServiceName getClusterServiceName();
    
    /**
     * {@link jp.ossc.nimbus.service.keepalive.Cluster クラスタ}サービスのメンバー情報のオプションキーを設定する。<p>
     *
     * @param key オプションキー
     */
    public void setClusterOptionKey(String key);
    
    /**
     * {@link jp.ossc.nimbus.service.keepalive.Cluster クラスタ}サービスのメンバー情報のオプションキーを取得する。<p>
     *
     * @return オプションキー
     */
    public String getClusterOptionKey();
    
    /**
     * {@link ClientConnection#connect()}実行時に、クラスタメンバが存在しなくても接続可能な柔軟な接続とするかどうかを設定する。<p>
     * デフォルトは、falseで、クラスタメンバが存在しない場合は、接続できない。<br>
     *
     * @param isFlexible 柔軟な接続とする場合、true
     */
    public void setFlexibleConnect(boolean isFlexible);
    
    /**
     * {@link ClientConnection#connect()}実行時に、クラスタメンバが存在しなくても接続可能な柔軟な接続とするかどうかを判定する。<p>
     *
     * @return trueの場合、柔軟な接続
     */
    public boolean isFlexibleConnect();
    
    public void setConnectErrorMessageId(String id);
    public String getConnectErrorMessageId();
}
