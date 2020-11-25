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
package jp.ossc.nimbus.service.keepalive;

import java.util.List;
import java.util.Set;

import jp.ossc.nimbus.core.*;

/**
 * {@link ClusterService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see ClusterService
 */
public interface ClusterServiceMBean extends ServiceBaseMBean{
    
    /**
     * このクラスタが稼動系に切り替わった旨のログメッセージID。<p>
     */
    public static final String MSG_ID_CHANGE_OPERATION_SYSTEM = "CLST_00001";
    
    /**
     * このクラスタが待機系に切り替わった旨のログメッセージID。<p>
     */
    public static final String MSG_ID_CHANGE_STANDBY_SYSTEM = "CLST_00002";
    
    /**
     * このクラスタが稼動系に切り替れなかった旨のログメッセージID。<p>
     */
    public static final String MSG_ID_FAILED_CHANGE_ACTIVE_SYSTEM = "CLST_00003";
    
    /**
     * クラスタ間のメッセージ送受信に失敗した旨のログメッセージID。<p>
     */
    public static final String MSG_ID_MESSAGE_IO_ERROR = "CLST_00004";
    
    /**
     * メンバが追加された旨のログメッセージID。<p>
     */
    public static final String MSG_ID_MESSAGE_MEMBER_ADD = "CLST_00005";
    
    /**
     * メンバが削除された旨のログメッセージID。<p>
     */
    public static final String MSG_ID_MESSAGE_MEMBER_REMOVE = "CLST_00006";
    
    /**
     * メンバが変更された旨のログメッセージID。<p>
     */
    public static final String MSG_ID_MESSAGE_MEMBER_CHANGE = "CLST_00007";
    
    /**
     * クライアントが追加された旨のログメッセージID。<p>
     */
    public static final String MSG_ID_MESSAGE_CLIENT_ADD = "CLST_00008";
    
    /**
     * クライアントが削除された旨のログメッセージID。<p>
     */
    public static final String MSG_ID_MESSAGE_CLIENT_REMOVE = "CLST_00009";
    
    /**
     * メンバが統合された旨のログメッセージID。<p>
     */
    public static final String MSG_ID_MESSAGE_MEMBAER_MERGE = "CLST_00010";
    
    /**
     * クラスタを組むサービスのサービス名を設定する。<p>
     *
     * @param name サービス名
     */
    public void setTargetServiceName(ServiceName name);
    
    /**
     * クラスタを組むサービスのサービス名を取得する。<p>
     *
     * @return サービス名
     */
    public ServiceName getTargetServiceName();
    
    /**
     * {@link ClusterListener}サービスのサービス名を設定する。<p>
     *
     * @param names ClusterListenerサービスのサービス名
     */
    public void setClusterListenerServiceNames(ServiceName[] names);
    
    /**
     * {@link ClusterListener}サービスのサービス名を取得する。<p>
     *
     * @return ClusterListenerサービスのサービス名
     */
    public ServiceName[] getClusterListenerServiceNames();
    
    /**
     * マルチキャストグループのIPアドレスを設定する。<p>
     *
     * @param ip IPアドレス
     */
    public void setMulticastGroupAddress(String ip);
    
    /**
     * マルチキャストグループのIPアドレスを取得する。<p>
     *
     * @return IPアドレス
     */
    public String getMulticastGroupAddress();
    
    /**
     * マルチキャストグループのポート番号を設定する。<p>
     * デフォルトは、1500。<br>
     *
     * @param port ポート番号
     */
    public void setMulticastPort(int port);
    
    /**
     * マルチキャストグループのポート番号を取得する。<p>
     *
     * @return ポート番号
     */
    public int getMulticastPort();
    
    /**
     * ユニキャストメンバーのIPアドレス及びポート番号を設定する。<p>
     *
     * @param addresses "IPアドレス:ポート番号"の配列。ポート番号が指定されていない場合は、{@link #getUnicastPort()}を使用する。
     */
    public void setUnicastMemberAddresses(String[] addresses);
    
    /**
     * ユニキャストメンバーのIPアドレス及びポート番号を取得する。<p>
     *
     * @return "IPアドレス:ポート番号"の配列
     */
    public String[] getUnicastMemberAddresses();
    
    /**
     * ユニキャストのポート番号を設定する。<p>
     * デフォルトは、1500。<br>
     *
     * @param port ポート番号
     */
    public void setUnicastPort(int port);
    
    /**
     * ユニキャストのポート番号を取得する。<p>
     *
     * @return ポート番号
     */
    public int getUnicastPort();
    
    /**
     * クライアントモードの際に、ユニキャストの受信ポート番号を任意のポート番号にするかどうかを設定する。<p>
     * デフォルトは、falseで、{@link #getUnicastPort()}を使用する。<br>
     *
     * @param isAnonymous 任意のポート番号にする場合、true
     */
    public void setAnonymousUnicastPort(boolean isAnonymous);
    
    /**
     * クライアントモードの際に、ユニキャストの受信ポート番号を任意のポート番号にするかどうかを判定する。<p>
     *
     * @return trueの場合、任意のポート番号にする
     */
    public boolean isAnonymousUnicastPort();
    
    /**
     * ソケットの受信バッファを設定する。<p>
     *
     * @param size ソケットの受信バッファ
     */
    public void setSocketReceiveBufferSize(int size);
    
    /**
     * ソケットの受信バッファを取得する。<p>
     *
     * @return ソケットの受信バッファ
     */
    public int getSocketReceiveBufferSize();
    
    /**
     * ソケットの送信バッファを設定する。<p>
     *
     * @param size ソケットの送信バッファ
     */
    public void setSocketSendBufferSize(int size);
    
    /**
     * ソケットの送信バッファを取得する。<p>
     *
     * @return ソケットの送信バッファ
     */
    public int getSocketSendBufferSize();
    
    /**
     * パケットの受信バッファサイズを設定する。<p>
     * デフォルトは、1024バイト。<br>
     *
     * @param size バッファサイズ
     */
    public void setReceiveBufferSize(int size);
    
    /**
     * パケットの受信バッファサイズを取得する。<p>
     *
     * @return バッファサイズ
     */
    public int getReceiveBufferSize();
    
    /**
     * マルチキャストパケットの有効期間を設定する。<p>
     *
     * @param ttl 有効期間
     */
    public void setTimeToLive(int ttl);
    
    /**
     * マルチキャストパケットの有効期間を取得する。<p>
     *
     * @return 有効期間
     */
    public int getTimeToLive();
    
    /**
     * ローカルIPアドレスを設定する。<p>
     *
     * @param ip IPアドレス
     */
    public void setLocalAddress(String ip);
    
    /**
     * ローカルIPアドレスを取得する。<p>
     *
     * @return IPアドレス
     */
    public String getLocalAddress();
    
    /**
     * バインドアドレスを設定する。<p>
     *
     * @param ip IPアドレス
     */
    public void setBindAddress(String ip);
    
    /**
     * バインドIPアドレスを取得する。<p>
     *
     * @return IPアドレス
     */
    public String getBindAddress();
    
    /**
     * ネットワークインタフェースを設定する。<p>
     *
     * @param names ネットワークインタフェース名
     */
    public void setNetworkInterfaces(String[] names);
    
    /**
     * ネットワークインタフェースを取得する。<p>
     *
     * @return ネットワークインタフェース名
     */
    public String[] getNetworkInterfaces();
    
    /**
     * 自分自身のIPアドレス以外の識別補足情報を設定する。<p>
     *
     * @param opt 識別補足情報
     */
    public void setOption(java.io.Serializable opt);
    
    /**
     * 自分自身のIPアドレス以外の識別補足情報を取得する。<p>
     *
     * @return 識別補足情報
     */
    public java.io.Serializable getOption();
    
    /**
     * 自分自身のIPアドレス以外の識別補足情報を設定する。<p>
     *
     * @param key キー
     * @param opt 識別補足情報
     */
    public void setOption(String key, java.io.Serializable opt);
    
    /**
     * 自分自身のIPアドレス以外の識別補足情報を取得する。<p>
     *
     * @param key キー
     * @return 識別補足情報
     */
    public java.io.Serializable getOption(String key);
    
    /**
     * 隣のクラスタサービスとハートビートを行う間隔[ms]を設定する。<p>
     * デフォルトは、1000[ms]。<br>
     *
     * @param interval ハートビートを行う間隔[ms]
     */
    public void setHeartBeatInterval(long interval);
    
    /**
     * 隣のクラスタサービスとハートビートを行う間隔[ms]を取得する。<p>
     *
     * @return ハートビートを行う間隔[ms]
     */
    public long getHeartBeatInterval();
    
    /**
     * 隣のクラスタサービスにハートビートをリクエストした時のタイムアウト[ms]を設定する。<p>
     * デフォルトは、500[ms]。<br>
     *
     * @param timeout タイムアウト[ms]
     */
    public void setHeartBeatResponseTimeout(long timeout);
    
    /**
     * 隣のクラスタサービスにハートビートをリクエストした時のタイムアウト[ms]を取得する。<p>
     *
     * @return タイムアウト[ms]
     */
    public long getHeartBeatResponseTimeout();
    
    /**
     * 隣のクラスタサービスとのハートビートに失敗した場合に、相手が死んだと見なすまでのリトライ回数を設定する。<p>
     * デフォルトは、1。<br>
     *
     * @param count リトライ回数
     */
    public void setHeartBeatRetryCount(int count);
    
    /**
     * 隣のクラスタサービスとのハートビートに失敗した場合に、相手が死んだと見なすまでのリトライ回数を取得する。<p>
     *
     * @return リトライ回数
     */
    public int getHeartBeatRetryCount();
    
    /**
     * クラスタに参加するリクエストを行った時のタイムアウト[ms]を設定する。<p>
     * デフォルトは、500[ms]。<br>
     *
     * @param timeout タイムアウト[ms]
     */
    public void setAddMemberResponseTimeout(long timeout);
    
    /**
     * クラスタに参加するリクエストを行った時のタイムアウト[ms]を取得する。<p>
     *
     * @return タイムアウト[ms]
     */
    public long getAddMemberResponseTimeout();
    
    /**
     * クラスタに参加するリクエストを行った時のリトライ回数を設定する。<p>
     * デフォルトは、0でリトライしない。<br>
     *
     * @param count リトライ回数
     */
    public void setAddMemberRetryCount(int count);
    
    /**
     * クラスタに参加するリクエストを行った時のリトライ回数を取得する。<p>
     *
     * @return リトライ回数
     */
    public int getAddMemberRetryCount();
    
    /**
     * 受信したパケットが分割されていた場合に、分割された残りパケットが消失したとみなすタイムアウト[ms]を設定する。<p>
     * デフォルトは、500[ms]。<br>
     *
     * @param timeout タイムアウト[ms]
     */
    public void setLostTimeout(long timeout);
    
    /**
     * 受信したパケットが分割されていた場合に、分割された残りパケットが消失したとみなすタイムアウト[ms]を取得する。<p>
     *
     * @return タイムアウト[ms]
     */
    public long getLostTimeout();
    
    /**
     * クラスタクライアントかどうかを設定する。<p>
     * クラスタクライアントになる場合は、trueに設定する。デフォルトは、false。<br>
     * クラスタクライアントは、クラスタのメンバには参加せずクラスタの状況通知のみ受ける。<br>
     *
     * @param isClient クラスタクライアントになる場合は、true
     */
    public void setClient(boolean isClient);
    
    /**
     * クラスタクライアントかどうかを判定する。<p>
     *
     * @return trueの場合、クラスタクライアント
     */
    public boolean isClient();
    
    /**
     * サービスの開始時に、クラスタに参加するかどうかを設定する。<p>
     * デフォルトは、true。<br>
     *
     * @param isJoin 参加する場合true
     */
    public void setJoinOnStart(boolean isJoin);
    
    /**
     * サービスの開始時に、クラスタに参加するかどうかを判定する。<p>
     *
     * @return trueの場合、参加する
     */
    public boolean isJoinOnStart();
    
    /**
     * クラスタのスレッドの優先度を設定する。<p>
     * デフォルトは-1で、スレッドに明示的に優先度を設定しない。<br>
     *
     * @param priority スレッドの優先度
     */
    public void setThreadPriority(int priority);
    
    /**
     * クラスタのスレッドの優先度を取得する。<p>
     *
     * @return スレッドの優先度
     */
    public int getThreadPriority();
    
    /**
     * クラスタに参加しているかどうかを判定する。<p>
     *
     * @return trueの場合、参加している
     */
    public boolean isJoin();
    
    /**
     * クラスタのメインであるかどうかを判定する。<p>
     *
     * @return trueの場合メイン
     */
    public boolean isMain();
    
    /**
     * クラスタのメイン疑惑であるかどうかを判定する。<p>
     *
     * @return trueの場合メイン疑惑
     */
    public boolean isMainDoubt();
    
    /**
     * クラスタのメイン疑惑であるかどうかを設定する。<p>
     *
     * @param isMainDoubt メイン疑惑の場合、true
     */
    public void setMainDoubt(boolean isMainDoubt);
    
    /**
     * 現在のクラスタメンバのUIDのリストを取得する。<p>
     *
     * @return 現在のクラスタメンバ
     */
    public List getMembers();
    
    /**
     * 現在のクライアントメンバのUIDのリストを取得する。<p>
     *
     * @return 現在のクライアントメンバ
     */
    public Set getClientMembers();
    
    /**
     * このサービスのUIDを取得する。<p>
     *
     * @return UID
     */
    public Object getUID();
    
    /**
     * クラスタに参加する。<p>
     */
    public void join() throws Exception;
    
    /**
     * クラスタから離脱する。<p>
     */
    public void leave();
    
    /**
     * 1メッセージあたりの最大分割数を取得する。<p>
     *
     * @return 最大分割数
     */
    public int getMaxWindowCount();
}