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
package jp.ossc.nimbus.service.publish.udp;

import java.util.Set;
import java.util.Map;
import java.util.Date;

import jp.ossc.nimbus.core.*;

/**
 * {@link ConnectionFactoryService}のMBeanインタフェース。<p>
 * 
 * @author M.Takata
 */
public interface ConnectionFactoryServiceMBean extends ServiceBaseMBean{
    
    public static final String MSG_ID_SEND_ERROR                = "PCFT_00001";
    public static final String MSG_ID_SEND_ERROR_RETRY_OVER     = "PCFT_00002";
    public static final String MSG_ID_RECEIVE_WARN              = "PCFT_00003";
    public static final String MSG_ID_RECEIVE_ERROR             = "PCFT_00004";
    public static final String MSG_ID_RESPONSE_ERROR            = "PCFT_00005";
    public static final String MSG_ID_CLIENT_LOST_MESSAGE_ERROR = "PCFT_00006";
    public static final String MSG_ID_SERVER_LOST_MESSAGE_ERROR = "PCFT_00007";
    public static final String MSG_ID_SERVER_CLOSE              = "PCFT_00008";
    public static final String MSG_ID_CLIENT_CONNECT            = "PCFT_00009";
    public static final String MSG_ID_CLIENT_CLOSE              = "PCFT_00010";
    
    /**
     * クライアント側でTCPソケットをバインドするローカルアドレスを指定するシステムプロパティ名を設定する。<br>
     * デフォルトは、指定なしで、クライアント側のループバックアドレスにバインドする。<br>
     *
     * @param name システムプロパティ名
     */
    public void setClientBindAddressPropertyName(String name);
    
    /**
     * クライアント側でTCPソケットをバインドするローカルアドレスを指定するシステムプロパティ名を取得する。<br>
     *
     * @return システムプロパティ名
     */
    public String getClientBindAddressPropertyName();
    
    /**
     * クライアント側でTCPソケットをバインドするローカルポートを指定するシステムプロパティ名を設定する。<br>
     * デフォルトは、指定なしで、クライアント側の任意のポートにバインドする。<br>
     *
     * @param name システムプロパティ名
     */
    public void setClientPortPropertyName(String name);
    
    /**
     * クライアント側でTCPソケットをバインドするローカルポートを指定するシステムプロパティ名を取得する。<br>
     *
     * @return システムプロパティ名
     */
    public String getClientPortPropertyName();
    
    /**
     * クライアント側でUDPソケットをバインドするローカルアドレスを指定するシステムプロパティ名を設定する。<br>
     * デフォルトは、指定なしで、ポート指定のみでバインドする。<br>
     *
     * @param name システムプロパティ名
     */
    public void setClientUDPBindAddressPropertyName(String name);
    
    /**
     * クライアント側でUDPソケットをバインドするローカルアドレスを指定するシステムプロパティ名を取得する。<br>
     *
     * @return システムプロパティ名
     */
    public String getClientUDPBindAddressPropertyName();
    
    /**
     * クライアント側でUDPパケットを受信するネットワークインタフェースを指定するシステムプロパティ名を設定する。<br>
     * デフォルトは、指定なしで、優先順位の高いネットワークインタフェース１つから受信する。<br>
     *
     * @param name システムプロパティ名
     */
    public void setClientUDPNetworkInterfacesPropertyName(String name);
    
    /**
     * クライアント側でUDPパケットを受信するネットワークインタフェースを指定するシステムプロパティ名を取得する。<br>
     *
     * @return システムプロパティ名
     */
    public String getClientUDPNetworkInterfacesPropertyName();
    
    /**
     * クライアント側で接続切断を検知した場合の再接続試行回数を設定する。<br>
     * デフォルトは、0で再接続しない。<br>
     * 再接続する場合は、{@link #setServerPort(int)}でサーバ側のポート番号を固定にする必要がある。<br>
     *
     * @param count 再接続試行回数
     */
    public void setClientReconnectCount(int count);
    
    /**
     * クライアント側で接続切断を検知した場合の再接続試行回数を取得する。<br>
     *
     * @return 再接続試行回数
     */
    public int getClientReconnectCount();
    
    /**
     * クライアント側で接続切断を検知した場合の再接続試行間隔[ms]を設定する。<br>
     * デフォルトは、0。<br>
     *
     * @param interval 再接続試行間隔[ms]
     */
    public void setClientReconnectInterval(long interval);
    
    /**
     * クライアント側で接続切断を検知した場合の再接続試行間隔[ms]を取得する。<br>
     *
     * @return 再接続試行間隔[ms]
     */
    public long getClientReconnectInterval();
    
    /**
     * クライアント側で接続切断を検知した場合に最後に受信したメッセージの受信時刻からどのくらいの時間[ms]だけ遡って再送要求を出すかを設定する。<p>
     * デフォルトは、0[ms]で最終受信時刻から再送要求する。<br>
     *
     * @param time 受信時刻から遡る時間[ms]
     */
    public void setClientReconnectBufferTime(long time);
    
    /**
     * クライアント側で接続切断を検知した場合に最後に受信したメッセージの受信時刻からどのくらいの時間[ms]だけ遡って再送要求を出すかを取得する。<p>
     *
     * @return 受信時刻から遡る時間[ms]
     */
    public long getClientReconnectBufferTime();
    
    /**
     * クライアント側で受信パケットを再利用する際の受信パケットバッファ数を設定する。<p>
     * デフォルトは、10。<br>
     *
     * @param size 受信パケットバッファ数
     */
    public void setClientPacketRecycleBufferSize(int size);
    
    /**
     * クライアント側で受信パケットを再利用する際の受信パケットバッファ数を取得する。<p>
     *
     * @return 受信パケットバッファ数
     */
    public int getClientPacketRecycleBufferSize();
    
    /**
     * クライアント側で受信ウィンドウを再利用する際の受信ウィンドウバッファ数を設定する。<p>
     * デフォルトは、200。<br>
     *
     * @param size 受信ウィンドウバッファ数
     */
    public void setClientWindowRecycleBufferSize(int size);
    
    /**
     * クライアント側で受信ウィンドウを再利用する際の受信ウィンドウバッファ数を取得する。<p>
     *
     * @return 受信ウィンドウバッファ数
     */
    public int getClientWindowRecycleBufferSize();
    
    /**
     * クライアント側で受信メッセージを再利用する際の受信メッセージバッファ数を設定する。<p>
     * デフォルトは、100。<br>
     *
     * @param size 受信メッセージバッファ数
     */
    public void setClientMessageRecycleBufferSize(int size);
    
    /**
     * クライアント側で受信メッセージバッファを再利用する際の受信メッセージバッファバッファ数を取得する。<p>
     *
     * @return 受信メッセージバッファバッファ数
     */
    public int getClientMessageRecycleBufferSize();
    
    /**
     * クライアント側で{@link Window}がロストしたと判断するまでのタイムアウトを設定する。<p>
     * デフォルトは1000[ms]。<br>
     *
     * @param timeout タイムアウト[ms]
     */
    public void setMissingWindowTimeout(long timeout);
    
    /**
     * クライアント側で{@link Window}がロストしたと判断するまでのタイムアウトを取得する。<p>
     *
     * @return タイムアウト[ms]
     */
    public long getMissingWindowTimeout();
    
    /**
     * クライアント側で{@link Window}がロストしたと判断するまでの滞留件数を設定する。<p>
     * デフォルトは0で、滞留件数ではロストしたと判断しない。<br>
     *
     * @param count 滞留件数
     */
    public void setMissingWindowCount(int count);
    
    /**
     * クライアント側で{@link Window}がロストしたと判断するまでの滞留件数を取得する。<p>
     *
     * @return 滞留件数
     */
    public int getMissingWindowCount();
    
    /**
     * クライアント側から後続のメッセージが来ていないかサーバ側へポーリングする間隔を設定する。<p>
     * デフォルトは5000[ms]。<br>
     *
     * @param interval ポーリングする間隔[ms]
     */
    public void setNewMessagePollingInterval(long interval);
    
    /**
     * クライアント側から後続のメッセージが来ていないかサーバ側へポーリングする間隔を取得する。<p>
     *
     * @return ポーリングする間隔[ms]
     */
    public long getNewMessagePollingInterval();
    
    /**
     * クライアントがサーバからの応答を待つタイムアウト[ms]を設定する。<p>
     * デフォルトは、30秒。<br>
     *
     * @param timeout タイムアウト
     */
    public void setClientResponseTimeout(long timeout);
    
    /**
     * クライアントがサーバからの応答を待つタイムアウト[ms]を取得する。<p>
     *
     * @return タイムアウト
     */
    public long getClientResponseTimeout();
    
    /**
     * サーバ側の接続待ち受けアドレスを設定する。<br>
     * 指定しない場合は、ローカルアドレス。<br>
     *
     * @param address アドレス
     */
    public void setServerAddress(String address);
    
    /**
     * サーバ側の接続待ち受けアドレスを取得する。<br>
     *
     * @return アドレス
     */
    public String getServerAddress();
    
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
     * サーバ側の接続待ち受けポート番号を設定する。<br>
     * デフォルトは、0で任意のポート番号。<br>
     *
     * @param port ポート番号
     */
    public void setServerPort(int port);
    
    /**
     * サーバ側の接続待ち受けポート番号を取得する。<br>
     *
     * @return ポート番号
     */
    public int getServerPort();
    
    /**
     * サーバソケットのバックログを設定する。<br>
     * デフォルトは0。<br>
     * 
     * @param backlog バックログ
     */
    public void setServerBacklog(int backlog);
    
    /**
     * サーバソケットのバックログを取得する。<br>
     * 
     * @return バックログ
     */
    public int getServerBacklog();
    
    /**
     * サーバソケットファクトリサービスのサービス名を設定する。<br>
     *
     * @param name サーバソケットファクトリサービスのサービス名
     */
    public void setServerSocketFactoryServiceName(ServiceName name);
    
    /**
     * サーバソケットファクトリサービスのサービス名を取得する。<br>
     *
     * @return サーバソケットファクトリサービスのサービス名
     */
    public ServiceName getServerSocketFactoryServiceName();
    
    /**
     * ソケットファクトリサービスのサービス名を設定する。<br>
     *
     * @param name ソケットファクトリサービスのサービス名
     */
    public void setSocketFactoryServiceName(ServiceName name);
    
    /**
     * ソケットファクトリサービスのサービス名を取得する。<br>
     *
     * @return ソケットファクトリサービスのサービス名
     */
    public ServiceName getSocketFactoryServiceName();
    
    /**
     * サーバ側のソケットでjava.nioを使った接続を行う場合のサーバ側のソケットに適用するソケットファクトリサービスのサービス名を設定する。<br>
     *
     * @param name ソケットファクトリサービスのサービス名
     */
    public void setNIOSocketFactoryServiceName(ServiceName name);
    
    /**
     * サーバ側のソケットでjava.nioを使った接続を行う場合のサーバ側のソケットに適用するソケットファクトリサービスのサービス名を取得する。<br>
     *
     * @return ソケットファクトリサービスのサービス名
     */
    public ServiceName getNIOSocketFactoryServiceName();
    
    /**
     * サーバ側のソケットでjava.nioを使った接続を行うかどうかを設定する。<p>
     * デフォルトは、false。<br>
     *
     * @param isNIO java.nioを使った接続を行う場合true
     */
    public void setNIO(boolean isNIO);
    
    /**
     * サーバ側のソケットでjava.nioを使った接続を行うかどうかを判定する。<p>
     *
     * @return trueの場合、java.nioを使った接続を行う
     */
    public boolean isNIO();
    
    /**
     * マルチキャスト配信を行う場合のバインドアドレスを設定する。<p>
     *
     * @param ip バインドアドレス
     */
    public void setSendBindAddress(String ip);
    
    /**
     * マルチキャスト配信を行う場合のバインドアドレスを取得する。<p>
     *
     * @return バインドアドレス
     */
    public String getSendBindAddress();
    
    /**
     * マルチキャスト配信を行う場合の送信ポート番号を設定する。<p>
     * デフォルトは、0で任意のポート番号。<br>
     * ユニキャスト配信でポート番号を指定した場合は、全てのクライアントに対する配信を同じ送信ソケットで配信する。<br>
     * 
     * @param port 送信ポート番号
     */
    public void setLocalPort(int port);
    
    /**
     * マルチキャスト配信を行う場合の送信ポート番号を取得する。<p>
     * 
     * @return 送信ポート番号
     */
    public int getLocalPort();
    
    /**
     * マルチキャスト配信を行う場合の配信アドレスを設定する。<p>
     * マルチキャストアドレスまたは、ブロードキャストアドレスを設定する。設定しない場合は、UDPユニキャスト配信となる。<br>
     * 
     * @param ip 配信アドレス
     */
    public void setMulticastGroupAddress(String ip);
    
    /**
     * マルチキャスト配信を行う場合の宛先アドレスを取得する。<p>
     * 
     * @return 宛先アドレス
     */
    public String getMulticastGroupAddress();
    
    /**
     * マルチキャスト配信を行う場合の宛先ポート番号を設定する。<p>
     * デフォルトは、2000。<br>
     * 
     * @param port 宛先ポート番号
     */
    public void setMulticastPort(int port);
    
    /**
     * マルチキャスト配信を行う場合の宛先ポート番号を取得する。<p>
     * 
     * @return 宛先ポート番号
     */
    public int getMulticastPort();
    
    /**
     * マルチキャスト配信を行う場合で、マルチキャストアドレスを指定した場合の、マルチキャストパケットの有効期間を設定する。<p>
     * デフォルトは、1。<br>
     *
     * @param ttl マルチキャストパケットの有効期間
     */
    public void setTimeToLive(int ttl);
    
    /**
     * マルチキャスト配信を行う場合で、マルチキャストアドレスを指定した場合の、マルチキャストパケットの有効期間を取得する。<p>
     *
     * @return マルチキャストパケットの有効期間
     */
    public int getTimeToLive();
    
    /**
     * ユニキャスト配信を行う場合の宛先ポート番号を設定する。<p>
     * デフォルトは、0で任意のポート番号。<br>
     * 
     * @param port 宛先ポート番号
     */
    public void setUnicastPort(int port);
    
    /**
     * ユニキャスト配信を行う場合の宛先ポート番号を取得する。<p>
     * 
     * @return 宛先ポート番号
     */
    public int getUnicastPort();
    
    /**
     * UDPパケットのサイズを設定する。<p>
     * デフォルトは、1024[byte]。<br>
     *
     * @param bytes UDPパケットのサイズ
     */
    public void setWindowSize(int bytes);
    
    /**
     * UDPパケットのサイズを取得する。<p>
     *
     * @return UDPパケットのサイズ
     */
    public int getWindowSize();
    
    /**
     * 送信パケットのキャッシュ時間を設定する。<p>
     * 受信側がパケットをロストしたと判断した時に、送信側にロストしたパケットを問い合わせて補間する。<br>
     * そのための送信側の補間用送信キャッシュに、送信したパケットを送信した時刻からどのくらいの間残しておくかを設定する。<br>
     * 受信側がパケットをロストしたと判断する時間は、{@link #setMissingWindowTimeout(long)}で設定するため、その時間より長く設定すべきである。<br>
     * デフォルトは、5000[ms]。<br>
     *
     * @param time キャッシュ時間[ms]
     */
    public void setSendMessageCacheTime(long time);
    
    /**
     * 送信パケットのキャッシュ時間を取得する。<p>
     *
     * @return キャッシュ時間[ms]
     */
    public long getSendMessageCacheTime();
    
    /**
     * 送信パケットをキャッシュする際のブロック件数を設定する。<p>
     * デフォルトは、100。<br>
     *
     * @param size ブロック件数
     */
    public void setMessageCacheBlockSize(int size);
    
    /**
     * 送信パケットをキャッシュする際のブロック件数を取得する。<p>
     *
     * @return ブロック件数
     */
    public int getMessageCacheBlockSize();
    
    /**
     * {@link jp.ossc.nimbus.service.publish.ClientConnectionFactory ClientConnectionFactory}リモートオブジェクトのJNDI名を設定する。<p>
     * デフォルトは、{@link jp.ossc.nimbus.service.publish.ClientConnectionFactory#DEFAULT_JNDI_NAME}。<br>
     *
     * @param name JNDI名
     */
    public void setJndiName(String name);
    
    /**
     * {@link jp.ossc.nimbus.service.publish.ClientConnectionFactory ClientConnectionFactory}リモートオブジェクトのJNDI名を取得する。<p>
     *
     * @return JNDI名
     */
    public String getJndiName();
    
    /**
     * {@link jp.ossc.nimbus.service.publish.ClientConnectionFactory ClientConnectionFactory}リモートオブジェクトをバインドする{@link jp.ossc.nimbus.service.repository.Repository Repository}サービスのサービス名を設定する。<p>
     * 
     * @param name Repositoryサービスのサービス名
     */
    public void setJndiRepositoryServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.publish.ClientConnectionFactory ClientConnectionFactory}リモートオブジェクトをバインドする{@link jp.ossc.nimbus.service.repository.Repository Repository}サービスのサービス名を取得する。<p>
     * 
     * @return Repositoryサービスのサービス名
     */
    public ServiceName getJndiRepositoryServiceName();
    
    /**
     * {@link jp.ossc.nimbus.service.publish.ClientConnectionFactory ClientConnectionFactory}リモートオブジェクトのRMI通信ポート番号を設定する。<p>
     *
     * @param port ポート番号
     */
    public void setRMIPort(int port);
    
    /**
     * {@link jp.ossc.nimbus.service.publish.ClientConnectionFactory ClientConnectionFactory}リモートオブジェクトのRMI通信ポート番号を取得する。<p>
     *
     * @return ポート番号
     */
    public int getRMIPort();
    
    /**
     * ユニキャスト配信の場合で、{@link jp.ossc.nimbus.service.publish.ServerConnection#send(Message)}で同期送信をする際に、各送信先へ並列に送信処理を行うスレッドの数を設定する。<p>
     * デフォルトは1で、各送信先へ直列に送信処理を行う。<br>
     *
     * @param threadSize スレッド数
     */
    public void setSendThreadSize(int threadSize);
    
    /**
     * ユニキャスト配信の場合で、{@link jp.ossc.nimbus.service.publish.ServerConnection#send(Message)}で同期送信をする際に、各送信先へ並列に送信処理を行うスレッドの数を取得する。<p>
     *
     * @return スレッド数
     */
    public int getSendThreadSize();
    
    /**
     * ユニキャスト配信の場合で、{@link jp.ossc.nimbus.service.publish.ServerConnection#send(Message)}で同期送信をする際に、各送信先への配信を並列送信するための{@link jp.ossc.nimbus.service.queue.Queue Queue}のサービス名を設定する。<p>
     * {@link #setSendThreadSize(int)}に、2以上を指定しない場合は、並列送信しないため、Queueは使用しない。<br>
     * 2以上を指定した場合で、この属性を指定しない場合は、内部でQueueを生成する。<br>
     *
     * @param name 並列送信用のQueueサービス名
     */
    public void setSendQueueServiceName(ServiceName name);
    
    /**
     * ユニキャスト配信の場合で、{@link jp.ossc.nimbus.service.publish.ServerConnection#send(Message)}で同期送信をする際に、各送信先への配信を並列送信するための{@link jp.ossc.nimbus.service.queue.Queue Queue}のサービス名を取得する。<p>
     *
     * @return 並列送信用のQueueサービス名
     */
    public ServiceName getSendQueueServiceName();
    
    /**
     * {@link jp.ossc.nimbus.service.publish.ServerConnection#sendAsynch(Message)}で非同期送信をする際に、各送信先へ並列に送信処理を行うスレッドの数を設定する。<p>
     * デフォルトは0で、非同期送信をサポートしない。<br>
     *
     * @param threadSize スレッド数
     */
    public void setAsynchSendThreadSize(int threadSize);
    
    /**
     * {@link jp.ossc.nimbus.service.publish.ServerConnection#sendAsynch(Message)}で非同期送信をする際に、各送信先へ並列に送信処理を行うスレッドの数を取得する。<p>
     *
     * @return スレッド数
     */
    public int getAsynchSendThreadSize();
    
    /**
     * {@link jp.ossc.nimbus.service.publish.ServerConnection#sendAsynch(Message)}で非同期送信をする際に使用する{@link jp.ossc.nimbus.service.queue.Queue Queue}サービス名を設定する。<p>
     * {@link #setAsynchSendThreadSize(int)}に、1以上を指定しない場合は、非同期送信をサポートしないため、Queueは使用しない。<br>
     * 1以上を指定した場合で、この属性を指定しない場合は、内部でQueueを生成する。<br>
     *
     * @param name 非同期送信のQueueサービス名
     */
    public void setAsynchSendQueueServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.publish.ServerConnection#sendAsynch(Message)}で非同期送信をする際にに使用する{@link jp.ossc.nimbus.service.queue.Queue Queue}サービス名を取得する。<p>
     *
     * @return 非同期送信のQueueサービス名
     */
    public ServiceName getAsynchSendQueueServiceName();
    
    /**
     * {@link jp.ossc.nimbus.service.publish.ServerConnection#sendAsynch(Message)}で非同期送信をする際に、並列送信を行うための{@link jp.ossc.nimbus.service.queue.Queue Queue}のファクトリサービス名を設定する。<p>
     * {@link #setAsynchSendThreadSize(int)}に、1以上を指定しない場合は、非同期送信をサポートしないため、Queueは使用しない。<br>
     * 1以上を指定した場合で、この属性を指定しない場合は、内部でQueueを生成する。<br>
     *
     * @param name 並列送信用のQueueのファクトリサービス名
     */
    public void setAsynchSendQueueFactoryServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.publish.ServerConnection#sendAsynch(Message)}で非同期送信をする際に、並列送信を行うための{@link jp.ossc.nimbus.service.queue.Queue Queue}のファクトリサービス名を取得する。<p>
     *
     * @return 並列送信用のQueueのファクトリサービス名
     */
    public ServiceName getAsynchSendQueueFactoryServiceName();
    
    /**
     * クライアントからの要求を受け付ける{@link jp.ossc.nimbus.service.queue.Queue Queue}サービスのサービス名を設定する。<p>
     * {@link #setRequestHandleThreadSize(int)}に、1以上を指定しない場合は、クライアントからの要求の並列処理をサポートしないため、Queueは使用しない。<br>
     * 1以上を指定した場合で、この属性を指定しない場合は、内部でQueueを生成する。<br>
     *
     * @param name クライアントからの要求の並列処理のQueueサービス名
     */
    public void setRequestHandleQueueServiceName(ServiceName name);
    
    /**
     * クライアントからの要求を受け付ける{@link jp.ossc.nimbus.service.queue.Queue Queue}サービスのサービス名を取得する。<p>
     *
     * @return クライアントからの要求の並列処理のQueueサービス名
     */
    public ServiceName getRequestHandleQueueServiceName();
    
    /**
     * クライアントからの要求の並列処理を行うスレッドの数を設定する。<p>
     * デフォルトは0で、並列処理をサポートしない。<br>
     *
     * @param threadSize スレッド数
     */
    public void setRequestHandleThreadSize(int threadSize);
    
    /**
     * クライアントからの要求の並列処理を行うスレッドの数を取得する。<p>
     *
     * @return スレッド数
     */
    public int getRequestHandleThreadSize();
    
    /**
     * 送信に失敗した場合に再送信を行う回数を設定する。<p>
     * デフォルトは、0で再送信しない。<br>
     * 
     * @param count 再送信回数
     */
    public void setMaxSendRetryCount(int count);
    
    /**
     * 送信に失敗した場合に再送信を行う回数を取得する。<p>
     *
     * @return 再送信回数
     */
    public int getMaxSendRetryCount();
    
    /**
     * 送信先からの要求に対して、応答を返すかどうかを設定する。<p>
     * デフォルトでは、falseで応答を返さない。<br>
     *
     * @param isAck 応答を返す場合true
     */
    public void setAcknowledge(boolean isAck);
    
    /**
     * 送信先からの要求に対して、応答を返すかどうかを判定する。<p>
     *
     * @return trueの場合、応答を返す
     */
    public boolean isAcknowledge();
    
    /**
     * 送信エラーが発生した場合に出力するログメッセージIDを設定する。<br>
     * デフォルトは、なしでログ出力しない。<br>
     * 
     * @param id ログメッセージID
     */
    public void setServerSendErrorMessageId(String id);
    
    /**
     * 送信エラーが発生した場合に出力するログメッセージIDを取得する。<br>
     * 
     * @return ログメッセージID
     */
    public String getServerSendErrorMessageId();
    
    /**
     * 送信エラーが発生し、規定のリトライ回数リトライしても成功しなかった場合に出力するログメッセージIDを設定する。<br>
     * デフォルトは、なしでログ出力しない。<br>
     * 
     * @param id ログメッセージID
     */
    public void setServerSendErrorRetryOverMessageId(String id);
    
    /**
     * 送信エラーが発生し、規定のリトライ回数リトライしても成功しなかった場合に出力するログメッセージIDを取得する。<br>
     * 
     * @return ログメッセージID
     */
    public String getServerSendErrorRetryOverMessageId();
    
    /**
     * クライアントからの要求に応答する際に、送信エラーが発生した場合に出力するログメッセージIDを設定する。<p>
     *
     * @param id ログメッセージID
     */
    public void setServerResponseErrorMessageId(String id);
    
    /**
     * クライアントからの要求に応答する際に、送信エラーが発生した場合に出力するログメッセージIDを取得する。<p>
     *
     * @return ログメッセージID
     */
    public String getServerResponseErrorMessageId();
    
    /**
     * クライアントからの補間要求に応答する際に、要求されたメッセージが補間できなかった場合に出力するログメッセージIDを設定する。<p>
     *
     * @param id ログメッセージID
     */
    public void setServerMessageLostErrorMessageId(String id);
    
    /**
     * クライアントからの補間要求に応答する際に、要求されたメッセージが補間できなかった場合に出力するログメッセージIDを取得する。<p>
     *
     * @return ログメッセージID
     */
    public String getServerMessageLostErrorMessageId();
    
    /**
     * クライアントが接続しにきた場合に出力するログメッセージIDを設定する。<p>
     *
     * @param id ログメッセージID
     */
    public void setClientConnectMessageId(String id);
    
    /**
     * クライアントが接続しにきた場合に出力するログメッセージIDを取得する。<p>
     *
     * @return ログメッセージID
     */
    public String getClientConnectMessageId();
    
    /**
     * クライアントが切断しにきた場合に出力するログメッセージIDを設定する。<p>
     *
     * @param id ログメッセージID
     */
    public void setClientCloseMessageId(String id);
    
    /**
     * クライアントが切断しにきた場合に出力するログメッセージIDを取得する。<p>
     *
     * @return ログメッセージID
     */
    public String getClientCloseMessageId();
    
    /**
     * サーバ側が終了した場合に出力するログメッセージIDを設定する。<p>
     *
     * @param id ログメッセージID
     */
    public void setClientServerCloseMessageId(String id);
    
    /**
     * サーバ側が終了した場合に出力するログメッセージIDを取得する。<p>
     *
     * @return ログメッセージID
     */
    public String getClientServerCloseMessageId();
    
    /**
     * サーバ側で送信メッセージを再利用する際の送信メッセージバッファ数を設定する。<p>
     * デフォルトは、100。<br>
     *
     * @param size 送信メッセージバッファ数
     */
    public void setServerMessageRecycleBufferSize(int size);
    
    /**
     * サーバ側で送信メッセージを再利用する際の送信メッセージバッファ数を取得する。<p>
     *
     * @return 送信メッセージバッファ数
     */
    public int getServerMessageRecycleBufferSize();
    
    /**
     * サーバ側で送信ウィンドウを再利用する際の送信ウィンドウバッファ数を設定する。<p>
     * デフォルトは、200。<br>
     *
     * @param size 送信ウィンドウバッファ数
     */
    public void setServerWindowRecycleBufferSize(int size);
    
    /**
     * サーバ側で送信ウィンドウを再利用する際の送信ウィンドウバッファ数を取得する。<p>
     *
     * @return 送信ウィンドウバッファ数
     */
    public int getServerWindowRecycleBufferSize();
    
    /**
     * {@link jp.ossc.nimbus.service.publish.MessageListener#onMessage(jp.ossc.nimbus.service.publish.Message)}で受信する際に、受信エラーが発生した場合に出力するログメッセージIDを設定する。<p>
     *
     * @param id ログメッセージID
     */
    public void setClientReceiveWarnMessageId(String id);
    
    /**
     * {@link jp.ossc.nimbus.service.publish.MessageListener#onMessage(jp.ossc.nimbus.service.publish.Message)}で受信する際に、受信エラーが発生した場合に出力するログメッセージIDを取得する。<p>
     *
     * @return ログメッセージID
     */
    public String getClientReceiveWarnMessageId();
    
    /**
     * {@link jp.ossc.nimbus.service.publish.MessageListener#onMessage(jp.ossc.nimbus.service.publish.Message)}で受信する際に、受信エラーが発生し、リトライアウトした場合に出力するログメッセージIDを設定する。<p>
     *
     * @param id ログメッセージID
     */
    public void setClientReceiveErrorMessageId(String id);
    
    /**
     * {@link jp.ossc.nimbus.service.publish.MessageListener#onMessage(jp.ossc.nimbus.service.publish.Message)}で受信する際に、受信エラーが発生し、リトライアウトした場合に出力するログメッセージIDを取得する。<p>
     *
     * @return ログメッセージID
     */
    public String getClientReceiveErrorMessageId();
    
    /**
     * サーバからの補間応答に要求したメッセージが存在しなかった場合に出力するログメッセージIDを設定する。<p>
     *
     * @param id ログメッセージID
     */
    public void setClientMessageLostErrorMessageId(String id);
    
    /**
     * サーバからの補間応答に要求したメッセージが存在しなかった場合に出力するログメッセージIDを取得する。<p>
     *
     * @return ログメッセージID
     */
    public String getClientMessageLostErrorMessageId();
    
    /**
     * メッセージの直列化/非直列化に使用する{@link jp.ossc.nimbus.service.io.Externalizer Externalizer}サービスのサービス名を設定する。<p>
     * 指定しない場合は、java.io.ObjectOutputStreamでメッセージの直列化/非直列化を行う。<br>
     *
     * @param name Externalizerサービスのサービス名
     */
    public void setExternalizerServiceName(ServiceName name);
    
    /**
     * メッセージの直列化/非直列化に使用する{@link jp.ossc.nimbus.service.io.Externalizer Externalizer}サービスのサービス名を取得する。<p>
     *
     * @return Externalizerサービスのサービス名
     */
    public ServiceName getExternalizerServiceName();
    
    /**
     * {@link jp.ossc.nimbus.service.publish.ServerConnectionListener ServerConnectionListener}のサービス名を設定する。<p>
     *
     * @param names ServerConnectionListenerのサービス名の配列
     */
    public void setServerConnectionListenerServiceNames(ServiceName[] names);
    
    /**
     * {@link jp.ossc.nimbus.service.publish.ServerConnectionListener ServerConnectionListener}のサービス名を取得する。<p>
     *
     * @return ServerConnectionListenerのサービス名の配列
     */
    public ServiceName[] getServerConnectionListenerServiceNames();
    
    /**
     * 送信件数を取得する。<p>
     *
     * @return 送信件数
     */
    public long getSendCount();
    
    /**
     * 送信パケット件数を取得する。<p>
     *
     * @return 送信パケット件数
     */
    public long getSendPacketCount();
    
    /**
     * 最大ウィンドウ数を取得する。<p>
     * メッセージを{@link #setWindowSize(int)}で指定したバイト数のパケットに分割して送信する。
     * この単位をウィンドウと呼び、この属性は、1メッセージあたりのウィンドウ数が最大となった値を示す。<br>
     *
     * @return 最大ウィンドウ数
     */
    public int getMaxWindowCount();
    
    /**
     * 平均ウィンドウ数を取得する。<p>
     * メッセージを{@link #setWindowSize(int)}で指定したバイト数のパケットに分割して送信する。
     * この単位をウィンドウと呼び、この属性は、1メッセージあたりのウィンドウ数の平均値を示す。<br>
     * 平均値が1に近い方が伝送処理効率が良いと言える。<br>
     *
     * @return 最大ウィンドウ数
     */
    public double getAverageWindowCount();
    
    /**
     * 送信件数をリセットする。<p>
     */
    public void resetSendCount();
    
    /**
     * 平均送信時間を取得する。<p>
     *
     * @return 平均送信時間[ms]
     */
    public long getAverageSendProcessTime();
    
    /**
     * 接続中のクライアントのjava.net.InetSocketAddressの集合を取得する。<br>
     *
     * @return クライアントのjava.net.InetSocketAddressの集合
     */
    public Set getClients();
    
    /**
     * 接続中のクライアントの数を取得する。<br>
     *
     * @return クライアントの数
     */
    public int getClientSize();
    
    /**
     * 接続中のクライアントで配信を行う対象となるクライアントのjava.net.InetSocketAddressの集合を取得する。<br>
     *
     * @return クライアントのjava.net.InetSocketAddressの集合
     */
    public Set getEnabledClients();
    
    /**
     * 接続中のクライアントで配信を行わない対象となるクライアントのjava.net.InetSocketAddressの集合を取得する。<br>
     *
     * @return クライアントのjava.net.InetSocketAddressの集合
     */
    public Set getDisabledClients();
    
    /**
     * 指定されたアドレス、ポート番号のクライアントの配信を有効にする。<p>
     * ポート番号の指定が0以下の場合は、ポート番号は任意とみなす。<br>
     *
     * @param address クライアントのアドレスまたはホスト名
     * @param port クライアントのポート番号
     */
    public void enabledClient(String address, int port);
    
    /**
     * 指定されたアドレス、ポート番号のクライアントの配信を無効にする。<p>
     * ポート番号の指定が0以下の場合は、ポート番号は任意とみなす。<br>
     *
     * @param address クライアントのアドレスまたはホスト名
     * @param port クライアントのポート番号
     */
    public void disabledClient(String address, int port);
    
    /**
     * 接続中のクライアント毎の送信件数を取得する。<br>
     *
     * @return クライアント毎の送信件数。キーがクライアントのjava.net.InetSocketAddress、値が送信件数のマップ
     */
    public Map getSendCountsByClient();
    
    /**
     * 接続中のクライアント毎の平均送信時間を取得する。<br>
     *
     * @return クライアント毎の平均送信時間[ms]。キーがクライアントのjava.net.InetSocketAddress、値が平均送信時間のマップ
     */
    public Map getAverageSendProcessTimesByClient();
    
    /**
     * 接続中のクライアント毎の送信件数をリセットする。<br>
     */
    public void resetSendCountsByClient();
    
    /**
     * 接続中のクライアント毎の新着要求件数を取得する。<br>
     *
     * @return クライアント毎の新着要求件数。キーがクライアントのjava.net.InetSocketAddress、値が新着要求件数のマップ
     */
    public Map getNewMessagePollingCountsByClient();
    
    /**
     * 接続中のクライアント毎の新着要求件数をリセットする。<br>
     */
    public void resetNewMessagePollingCountsByClient();
    
    /**
     * 接続中のクライアント毎の補間要求件数を取得する。<br>
     *
     * @return クライアント毎の補間要求件数。キーがクライアントのjava.net.InetSocketAddress、値が補間要求件数のマップ
     */
    public Map getInterpolateRequestCountsByClient();
    
    /**
     * 接続中のクライアント毎の補間要求件数をリセットする。<br>
     */
    public void resetInterpolateRequestCountsByClient();
    
    /**
     * 接続中のクライアント毎のロスト件数を取得する。<br>
     *
     * @return クライアント毎のロスト件数。キーがクライアントのjava.net.InetSocketAddress、値がロスト件数のマップ
     */
    public Map getLostCountsByClient();
    
    /**
     * 接続中のクライアント毎のロスト件数をリセットする。<br>
     */
    public void resetLostCountsByClient();
    
    /**
     * 指定されたアドレス、ポート番号のクライアントが登録しているサブジェクトを取得する。<p>
     *
     * @param address クライアントのアドレスまたはホスト名
     * @param port クライアントのポート番号
     * @return サブジェクトの集合
     */
    public Set getSubjects(String address, int port);
    
    /**
     * 指定されたアドレス、ポート番号のクライアントが、指定されたサブジェクトに対して登録されているキーを取得する。<p>
     *
     * @param address クライアントのアドレスまたはホスト名
     * @param port クライアントのポート番号
     * @param subject サブジェクト
     * @return キーの集合
     */
    public Set getKeys(String address, int port, String subject);
    
    /**
     * 補間用送信キャッシュにキャッシュされているメッセージの中で、最も古いメッセージの通番を取得する。<p>
     *
     * @return メッセージ通番
     */
    public int getMostOldSendMessageCacheSequence();
    
    /**
     * 補間用送信キャッシュにキャッシュされているメッセージの中で、最も古いメッセージの送信時刻を取得する。<p>
     *
     * @return メッセージの送信時刻
     */
    public Date getMostOldSendMessageCacheTime();
    
    /**
     * 補間用送信キャッシュにキャッシュされているメッセージの件数を取得する。<p>
     *
     * @return メッセージ件数
     */
    public int getSendMessageCacheSize();
    
    /**
     * 非同期送信処理の平均処理時間[ms]を取得する。<p>
     *
     * @return 平均処理時間[ms]
     */
    public long getAverageAsynchSendProcessTime();
    
    /**
     * クライアントからの要求に対する処理の平均処理時間[ms]を取得する。<p>
     *
     * @return 平均処理時間[ms]
     */
    public long getAverageRequestHandleProcessTime();
    
    /**
     * メッセージの再利用率を取得する。<p>
     *
     * @return メッセージの再利用率
     */
    public double getMessageRecycleRate();
    
    /**
     * ウィンドウの再利用率を取得する。<p>
     *
     * @return ウィンドウの再利用率
     */
    public double getWindowRecycleRate();
}