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
package jp.ossc.nimbus.service.publish.local;

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
    public static final String MSG_ID_SERVER_CLOSE          = "PCFT_00008";
    
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
     * {@link jp.ossc.nimbus.service.publish.ClientConnection ClientConnection}のIDを生成する{@link jp.ossc.nimbus.service.sequence.Sequence Sequence}サービスのサービス名を設定する。<p>
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
     * {@link jp.ossc.nimbus.service.publish.ServerConnection#sendAsynch(Message)}で非同期送信をする際に、並列送信を行うための{@link jp.ossc.nimbus.service.queue.Queue Queue}のファクトリサービス名を設定する。<p>
     * {@link #setAsynchSendThreadSize(int)}に、1以上を指定しない場合は、非同期送信をサポートしないため、Queueは使用しない。<br>
     * 1以上を指定した場合で、この属性を指定しない場合は、内部でQueueを生成する。<br>
     *
     * @param name 並列送信用のQueueサービス名
     */
    public void setAsynchSendQueueFactoryServiceName(ServiceName name);
    
    /**
     * {@link jp.ossc.nimbus.service.publish.ServerConnection#sendAsynch(Message)}で非同期送信をする際に、並列送信を行うための{@link jp.ossc.nimbus.service.queue.Queue Queue}のファクトリサービス名を取得する。<p>
     *
     * @return 並列送信用のQueueサービス名
     */
    public ServiceName getAsynchSendQueueFactoryServiceName();
    
    /**
     * 送信パケットのキャッシュ時間を設定する。<p>
     * {@link jp.ossc.nimbus.service.publish.ClientConnection#startReceive(long)}で遡って受信するために、送信側で送信キャッシュに、送信したパケットを送信した時刻からどのくらいの間残しておくかを設定する。<br>
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
     * 接続中のクライアントのIDの集合を取得する。<br>
     *
     * @return クライアントのIDの集合
     */
    public Set getClients();
    
    /**
     * 接続中のクライアントの数を取得する。<br>
     *
     * @return クライアントの数
     */
    public int getClientSize();
    
    /**
     * 接続中のクライアントで配信を行う対象となるクライアントのIDの集合を取得する。<br>
     *
     * @return クライアントのIDの集合
     */
    public Set getEnabledClients();
    
    /**
     * 接続中のクライアントで配信を行わない対象となるクライアントのIDの集合を取得する。<br>
     *
     * @return クライアントのIDの集合
     */
    public Set getDisabledClients();
    
    /**
     * 指定されたIDのクライアントの配信を有効にする。<p>
     * ポート番号の指定が0以下の場合は、ポート番号は任意とみなす。<br>
     *
     * @param id クライアントのID
     */
    public void enabledClient(Object id);
    
    /**
     * 指定されたIDのクライアントの配信を無効にする。<p>
     * ポート番号の指定が0以下の場合は、ポート番号は任意とみなす。<br>
     *
     * @param id クライアントのID
     */
    public void disabledClient(Object id);
    
    /**
     * 接続中のクライアント毎の送信件数を取得する。<br>
     *
     * @return クライアント毎の送信件数。キーがクライアントのID、値が送信件数のマップ
     */
    public Map getSendCountsByClient();
    
    /**
     * 接続中のクライアント毎の送信件数をリセットする。<br>
     */
    public void resetSendCountsByClient();
    
    /**
     * 指定されたIDのクライアントが登録しているサブジェクトを取得する。<p>
     *
     * @param id クライアントのID
     * @return サブジェクトの集合
     */
    public Set getSubjects(Object id);
    
    /**
     * 指定されたIDのクライアントが、指定されたサブジェクトに対して登録されているキーを取得する。<p>
     *
     * @param id クライアントのID
     * @param subject サブジェクト
     * @return キーの集合
     */
    public Set getKeys(Object id, String subject);
}