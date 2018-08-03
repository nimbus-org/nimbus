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
package jp.ossc.nimbus.service.publish.tcp;

import java.util.Set;
import java.util.Map;

import jp.ossc.nimbus.core.*;

/**
 * {@link ConnectionFactoryService}のMBeanインタフェース。<p>
 * 
 * @author M.Takata
 */
public interface ConnectionFactoryServiceMBean extends ServiceBaseMBean{
    
    public static final String MSG_ID_SEND_ERROR            = "PCFT_00001";
    public static final String MSG_ID_SEND_ERROR_RETRY_OVER = "PCFT_00002";
    public static final String MSG_ID_RECEIVE_WARN          = "PCFT_00003";
    public static final String MSG_ID_RECEIVE_ERROR         = "PCFT_00004";
    public static final String MSG_ID_SERVER_CLOSE          = "PCFT_00008";
    public static final String MSG_ID_SERVER_CLIENT_CONNECT = "PCFT_00009";
    public static final String MSG_ID_SERVER_CLIENT_CLOSED  = "PCFT_00010";
    public static final String MSG_ID_SERVER_CLIENT_CLOSE   = "PCFT_00011";
    public static final String MSG_ID_CLIENT_START_RECEIVE  = "PCFT_00012";
    public static final String MSG_ID_CLIENT_STOP_RECEIVE   = "PCFT_00013";
    public static final String MSG_ID_SERVER_START_RECEIVE  = "PCFT_00014";
    public static final String MSG_ID_SERVER_STOP_RECEIVE   = "PCFT_00015";
    public static final String MSG_ID_CLIENT_CONNECT        = "PCFT_00016";
    public static final String MSG_ID_CLIENT_CLOSE          = "PCFT_00017";
    public static final String MSG_ID_CLIENT_CLOSED         = "PCFT_00018";
    
    /**
     * クライアント側でバインドするローカルアドレスを指定するシステムプロパティ名を設定する。<br>
     * デフォルトは、指定なしで、クライアント側のループバックアドレスにバインドする。<br>
     *
     * @param name システムプロパティ名
     */
    public void setClientAddressPropertyName(String name);
    
    /**
     * クライアント側でバインドするローカルアドレスを指定するシステムプロパティ名を取得する。<br>
     *
     * @return システムプロパティ名
     */
    public String getClientAddressPropertyName();
    
    /**
     * クライアント側でバインドするローカルポートを指定するシステムプロパティ名を設定する。<br>
     * デフォルトは、指定なしで、クライアント側の任意のポートにバインドする。<br>
     *
     * @param name システムプロパティ名
     */
    public void setClientPortPropertyName(String name);
    
    /**
     * クライアント側でバインドするローカルポートを指定するシステムプロパティ名を取得する。<br>
     *
     * @return システムプロパティ名
     */
    public String getClientPortPropertyName();
    
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
     * サーバ側の接続待ち受けポート番号を設定する。<br>
     * デフォルトは、0で任意のポート番号。<br>
     * 
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
     * でjava.nioを使った接続を行う場合のサーバ側のソケットに適用するソケットファクトリサービスのサービス名を設定する。<br>
     *
     * @param name ソケットファクトリサービスのサービス名
     */
    public void setNIOSocketFactoryServiceName(ServiceName name);
    
    /**
     * でjava.nioを使った接続を行う場合のサーバ側のソケットに適用するソケットファクトリサービスのサービス名を取得する。<br>
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
     * {@link jp.ossc.nimbus.service.publish.ServerConnection#send(Message)}で同期送信をする際に、各送信先へ並列に送信処理を行うスレッドの数を設定する。<p>
     * デフォルトは1で、各送信先へ直列に送信処理を行う。<br>
     *
     * @param threadSize スレッド数
     */
    public void setSendThreadSize(int threadSize);
    
    /**
     * {@link jp.ossc.nimbus.service.publish.ServerConnection#send(Message)}で同期送信をする際に、各送信先へ並列に送信処理を行うスレッドの数を取得する。<p>
     *
     * @return スレッド数
     */
    public int getSendThreadSize();
    
    /**
     * {@link jp.ossc.nimbus.service.publish.ServerConnection#send(Message)}で同期送信をする際に、並列送信を行うための{@link jp.ossc.nimbus.service.queue.Queue Queue}のサービス名を設定する。<p>
     * {@link #setSendThreadSize(int)}に、2以上を指定しない場合は、並列送信しないため、Queueは使用しない。<br>
     * 2以上を指定した場合で、この属性を指定しない場合は、内部でQueueを生成する。<br>
     *
     * @param name 並列送信用のQueueサービス名
     */
    public void setSendQueueServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.publish.ServerConnection#send(Message)}で同期送信をする際に、並列送信を行うための{@link jp.ossc.nimbus.service.queue.Queue Queue}のサービス名を取得する。<p>
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
     * {@link jp.ossc.nimbus.service.publish.ServerConnection#sendAsynch(Message)}で非同期送信をする際に使用する{@link jp.ossc.nimbus.service.queue.Queue Queue}のサービス名を設定する。<p>
     * {@link #setAsynchSendThreadSize(int)}に、1以上を指定しない場合は、非同期送信をサポートしないため、Queueは使用しない。<br>
     * 1以上を指定した場合で、この属性を指定しない場合は、内部でQueueを生成する。<br>
     *
     * @param name 非同期送信用のQueueサービス名
     */
    public void setAsynchSendQueueServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.publish.ServerConnection#sendAsynch(Message)}で非同期送信をする際に使用する{@link jp.ossc.nimbus.service.queue.Queue Queue}のサービス名を取得する。<p>
     *
     * @return 非同期送信用のQueueサービス名
     */
    public ServiceName getAsynchSendQueueServiceName();
    
    /**
     * {@link jp.ossc.nimbus.service.publish.ServerConnection#sendAsynch(Message)}で非同期送信をする際に、並列送信を行うための{@link jp.ossc.nimbus.service.queue.Queue Queue}のファクトリサービス名を設定する。<p>
     * {@link #setAsynchSendThreadSize(int)}に、1以上を指定しない場合は、非同期送信をサポートしないため、Queueは使用しない。<br>
     * 1以上を指定した場合で、この属性を指定しない場合は、内部でQueueを生成する。<br>
     *
     * @param name 並列送信用のQueueファクトリサービス名
     */
    public void setAsynchSendQueueFactoryServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.publish.ServerConnection#sendAsynch(Message)}で非同期送信をする際に、並列送信を行うための{@link jp.ossc.nimbus.service.queue.Queue Queue}のファクトリサービス名を取得する。<p>
     *
     * @return 並列送信用のQueueファクトリサービス名
     */
    public ServiceName getAsynchSendQueueFactoryServiceName();
    
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
     * 送信メッセージのキャッシュ時間を設定する。<p>
     * {@link jp.ossc.nimbus.service.publish.ClientConnection#startReceive(long)}で遡って受信するために、送信側で送信キャッシュに、送信したメッセージを送信した時刻からどのくらいの間残しておくかを設定する。<br>
     * デフォルトは、5000[ms]。<br>
     *
     * @param time キャッシュ時間[ms]
     */
    public void setSendMessageCacheTime(long time);
    
    /**
     * 送信メッセージのキャッシュ時間を取得する。<p>
     *
     * @return キャッシュ時間[ms]
     */
    public long getSendMessageCacheTime();
    
    /**
     * 送信メッセージをまとめて送信するための滞留時間を設定する。<p>
     * デフォルトは、0で滞留させない。<br>
     *
     * @param time 送信滞留時間[ms]
     */
    public void setSendBufferTime(long time);
    
    /**
     * 送信メッセージをまとめて送信するための滞留時間を取得する。<p>
     *
     * @return 送信滞留時間[ms]
     */
    public long getSendBufferTime();
    
    /**
     * 送信メッセージをまとめて送信するための滞留バイト数を設定する。<p>
     * デフォルトは、0で滞留させない。<br>
     *
     * @param bytes 送信滞留バイト数
     */
    public void setSendBufferSize(long bytes);
    
    /**
     * 送信メッセージをまとめて送信するための滞留バイト数を取得する。<p>
     *
     * @return 送信滞留バイト数
     */
    public long getSendBufferSize();
    
    /**
     * 送信メッセージをまとめて送信する場合に、送信滞留チェック間隔[ms]を設定する。<p>
     * デフォルトは、1000[ms]。<br>
     *
     * @param interval 送信滞留チェック間隔[ms]
     */
    public void setSendBufferTimeoutInterval(long interval);
    
    /**
     * 送信メッセージをまとめて送信する場合に、送信滞留チェック間隔[ms]を取得する。<p>
     *
     * @return 送信滞留チェック間隔[ms]
     */
    public long getSendBufferTimeoutInterval();
    
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
     * クライアントが接続しにきた場合に出力するログメッセージIDを設定する。<p>
     *
     * @param id ログメッセージID
     */
    public void setServerClientConnectMessageId(String id);
    
    /**
     * クライアントが接続しにきた場合に出力するログメッセージIDを取得する。<p>
     *
     * @return ログメッセージID
     */
    public String getServerClientConnectMessageId();
    
    /**
     * クライアントが切断しにきた場合に出力するログメッセージIDを設定する。<p>
     *
     * @param id ログメッセージID
     */
    public void setServerClientClosedMessageId(String id);
    
    /**
     * クライアントが切断しにきた場合に出力するログメッセージIDを取得する。<p>
     *
     * @return ログメッセージID
     */
    public String getServerClientClosedMessageId();
    
    /**
     * クライアントを切断した場合に出力するログメッセージIDを設定する。<p>
     *
     * @param id ログメッセージID
     */
    public void setServerClientCloseMessageId(String id);
    
    /**
     * クライアントを切断した場合に出力するログメッセージIDを取得する。<p>
     *
     * @return ログメッセージID
     */
    public String getServerClientCloseMessageId();
    
    /**
     * サーバが受信開始した場合に出力するログメッセージIDを設定する。<p>
     *
     * @param id ログメッセージID
     */
    public void setServerStartReceiveMessageId(String id);
    
    /**
     * サーバが受信開始した場合に出力するログメッセージIDを取得する。<p>
     *
     * @return ログメッセージID
     */
    public String getServerStartReceiveMessageId();
    
    /**
     * サーバが受信停止した場合に出力するログメッセージIDを設定する。<p>
     *
     * @param id ログメッセージID
     */
    public void setServerStopReceiveMessageId(String id);
    
    /**
     * サーバが受信停止した場合に出力するログメッセージIDを取得する。<p>
     *
     * @return ログメッセージID
     */
    public String getServerStopReceiveMessageId();
    
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
     * クライアントが受信開始した場合に出力するログメッセージIDを設定する。<p>
     *
     * @param id ログメッセージID
     */
    public void setClientStartReceiveMessageId(String id);
    
    /**
     * クライアントが受信開始した場合に出力するログメッセージIDを取得する。<p>
     *
     * @return ログメッセージID
     */
    public String getClientStartReceiveMessageId();
    
    /**
     * クライアントが受信停止した場合に出力するログメッセージIDを設定する。<p>
     *
     * @param id ログメッセージID
     */
    public void setClientStopReceiveMessageId(String id);
    
    /**
     * クライアントが受信停止した場合に出力するログメッセージIDを取得する。<p>
     *
     * @return ログメッセージID
     */
    public String getClientStopReceiveMessageId();
    
    /**
     * クライアントが接続を要求した場合に出力するログメッセージIDを設定する。<p>
     *
     * @param id ログメッセージID
     */
    public void setClientConnectMessageId(String id);
    
    /**
     * クライアントが接続を要求した場合に出力するログメッセージIDを取得する。<p>
     *
     * @return ログメッセージID
     */
    public String getClientConnectMessageId();
    
    /**
     * クライアントが切断した場合に出力するログメッセージIDを設定する。<p>
     *
     * @param id ログメッセージID
     */
    public void setClientCloseMessageId(String id);
    
    /**
     * クライアントが切断した場合に出力するログメッセージIDを取得する。<p>
     *
     * @return ログメッセージID
     */
    public String getClientCloseMessageId();
    
    /**
     * クライアントが切断された場合に出力するログメッセージIDを設定する。<p>
     *
     * @param id ログメッセージID
     */
    public void setClientClosedMessageId(String id);
    
    /**
     * クライアントが切断された場合に出力するログメッセージIDを取得する。<p>
     *
     * @return ログメッセージID
     */
    public String getClientClosedMessageId();
    
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
     * 送信件数をリセットする。<p>
     */
    public void resetSendCount();
    
    /**
     * 平均送信時間を取得する。<p>
     *
     * @return 平均送信時間[ms]
     */
    public double getAverageSendProcessTime();
    
    /**
     * 平均送信バイト数[byte]を取得する。<p>
     *
     * @return 平均送信バイト数[byte]
     */
    public double getAverageSendBytes();
    
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
     * 接続中のクライアント毎の平均送信バイト数を取得する。<br>
     *
     * @return クライアント毎の平均送信バイト数[byte]。キーがクライアントのjava.net.InetSocketAddress、値が平均送信バイト数のマップ
     */
    public Map getAverageSendBytesByClient();
    
    /**
     * 接続中のクライアント毎の送信件数をリセットする。<br>
     */
    public void resetSendCountsByClient();
    
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
}