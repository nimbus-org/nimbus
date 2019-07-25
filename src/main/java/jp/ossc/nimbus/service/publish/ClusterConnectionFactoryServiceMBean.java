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
 * {@link ClusterConnectionFactoryService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see ClusterConnectionFactoryService
 */
public interface ClusterConnectionFactoryServiceMBean extends ServiceBaseMBean{
    
    public static final String MSG_ID_CONNECT_ERROR = "PCCF_00001";
    public static final String MSG_ID_RECONNECT     = "PCCF_00003";
    public static final String MSG_ID_NOCONNECT_ERROR = "PCCF_00004";
    public static final String MSG_ID_CONNECTION_GET_ERROR = "PCCF_00005";
    
    /**
     * {@link ClientConnectionFactory ClientConnectionFactory}リモートオブジェクトのJNDI名を設定する。<p>
     * デフォルトは、{@link ClientConnectionFactory#DEFAULT_JNDI_NAME}。<br>
     *
     * @param name JNDI名
     */
    public void setJndiName(String name);
    
    /**
     * {@link ClientConnectionFactory}リモートオブジェクトのJNDI名を取得する。<p>
     *
     * @return JNDI名
     */
    public String getJndiName();
    
    /**
     * {@link ClientConnectionFactory}リモートオブジェクトをバインドする{@link jp.ossc.nimbus.service.repository.Repository Repository}サービスのサービス名を設定する。<p>
     * 
     * @param name Repositoryサービスのサービス名
     */
    public void setJndiRepositoryServiceName(ServiceName name);
    
    /**
     * {@link ClientConnectionFactory}リモートオブジェクトをバインドする{@link jp.ossc.nimbus.service.repository.Repository Repository}サービスのサービス名を取得する。<p>
     * 
     * @return Repositoryサービスのサービス名
     */
    public ServiceName getJndiRepositoryServiceName();
    
    /**
     * {@link ClientConnectionFactory}リモートオブジェクトのRMI通信ポート番号を設定する。<p>
     *
     * @param port ポート番号
     */
    public void setRMIPort(int port);
    
    /**
     * {@link ClientConnectionFactory}リモートオブジェクトのRMI通信ポート番号を取得する。<p>
     *
     * @return ポート番号
     */
    public int getRMIPort();
    
    /**
     * {@link jp.ossc.nimbus.service.keepalive.Cluster Cluster}サービスのサービス名を設定する。<p>
     * クラスタのメンバー情報に、{@link ClientConnectionFactory}リモートオブジェクトを設定する事で、クラスタに参加しているClientConnectionFactoryをクライアント側で共有する。<br>
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
     * {@link jp.ossc.nimbus.service.keepalive.Cluster クラスタ}サービスのクラスタ参加を制御するかどうかを設定する。<p>
     * デフォルトは、trueで制御する。<br>
     *
     * @param isJoin 制御する場合、true
     */
    public void setClusterJoin(boolean isJoin);
    
    /**
     * {@link jp.ossc.nimbus.service.keepalive.Cluster クラスタ}サービスのクラスタ参加を制御するかどうかを判定する。<p>
     *
     * @return trueの場合、制御する
     */
    public boolean isClusterJoin();
    
    /**
     * クラスタ化したい{@link ClientConnectionFactory}サービスのサービス名を設定する。<p>
     *
     * @param name ClientConnectionFactoryサービスのサービス名
     */
    public void setClientConnectionFactoryServiceName(ServiceName name);
    
    /**
     * クラスタ化したい{@link ClientConnectionFactory}サービスのサービス名を取得する。<p>
     *
     * @return ClientConnectionFactoryサービスのサービス名
     */
    public ServiceName getClientConnectionFactoryServiceName();
    
    /**
     * {@link jp.ossc.nimbus.service.publish.ClientConnection ClientConnection}のIDを生成する{@link jp.ossc.nimbus.service.sequence.Sequence Sequence}サービスのサービス名を設定する。<p>
     * 複数の接続に対して、異なるIDを振りたい場合に設定する。<br>
     * 
     * @param name Sequenceサービスのサービス名
     */
    public void setSequenceServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.publish.ClientConnection ClientConnection}のIDを生成する{@link jp.ossc.nimbus.service.sequence.Sequence Sequence}サービスのサービス名を取得する。<p>
     * 
     * @return Sequenceサービスのサービス名
     */
    public ServiceName getSequenceServiceName();
    
    /**
     * 分散クラスタにするかどうかを設定する。<p>
     * trueにすると分散クラスタとなり、クライアントは、接続台数の少ないクラスタメンバに接続し、サーバに対して分散して接続する。<br>
     * デフォルトはfalseで、全てのクライアントが主系となっているクラスタメンバに接続する。<br>
     *
     * @param isDistribute 分散クラスタにする場合true
     */
    public void setDistribute(boolean isDistribute);
    
    /**
     * 分散クラスタにするかどうかを判定する。<p>
     *
     * @return trueの場合、分散クラスタ
     */
    public boolean isDistribute();
    
    /**
     * 多重サーバ配信するかどうかを設定する。<p>
     * trueにすると多重サーバ配信となり、クライアントは複数のサーバからメッセージを同時に受信する。<br>
     * デフォルトはfalseで、クライアントは、どこか１つのサーバから受信する。<br>
     *
     * @param isMultiple 多重サーバ配信する場合、true
     */
    public void setMultiple(boolean isMultiple);
    
    /**
     * 多重サーバ配信するかどうかを判定する。<p>
     *
     * @return trueの場合、多重サーバ配信する
     */
    public boolean isMultiple();
    
    /**
     * 自分自身からのメッセージを受信するかどうかを設定する。<p>
     * デフォルトは、falseで受信しない。<br>
     * 
     * @param isReceive 受信する場合true
     */
    public void setReceiveOwnMessage(boolean isReceive);
    
    /**
     * 自分自身からのメッセージを受信するかどうかを判定する。<p>
     * 
     * @return trueの場合、受信する
     */
    public boolean isReceiveOwnMessage();
    
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
    
    /**
     * フェイルオーバー時に、どのくらいの時間[ms]だけ遡って再送要求を出すかを設定する。<p>
     * デフォルトは、0[ms]で遡らず再送要求する。<br>
     *
     * @param time 遡る時間[ms]
     */
    public void setFailoverBufferTime(long time);
    
    /**
     * フェイルオーバー時に、どのくらいの時間[ms]だけ遡って再送要求を出すかを取得する。<p>
     *
     * @return 遡る時間[ms]
     */
    public long getFailoverBufferTime();
    
    /**
     * フェイルオーバー時に、最後に受信したメッセージの受信時刻を起点に、再送要求をするかどうかを設定する。<p>
     * デフォルトは、trueで、最後に受信したメッセージの受信時刻を起点に、再送要求をする。falseにすると、現在時刻を起点にする。<br>
     * 
     * @param isStartReceive 最後に受信したメッセージの受信時刻を起点にする場合、true
     */
    public void setStartReceiveFromLastReceiveTime(boolean isStartReceive);
    
    /**
     * フェイルオーバー時に、最後に受信したメッセージの受信時刻を起点に、再送要求をするかどうかを判定する。<p>
     * 
     * @return trueの場合、最後に受信したメッセージの受信時刻を起点にする
     */
    public boolean isStartReceiveFromLastReceiveTime();
    
    public void setClientConnectErrorMessageId(String id);
    public String getClientConnectErrorMessageId();
    
    public void setClientReconnectMessageId(String id);
    public String getClientReconnectMessageId();
    
    public void setClientNoConnectErrorMessageId(String id);
    public String getClientNoConnectErrorMessageId();
    
    public void setClientConnectionGetErrorMessageId(String id);
    public String getClientConnectionGetErrorMessageId();
}
