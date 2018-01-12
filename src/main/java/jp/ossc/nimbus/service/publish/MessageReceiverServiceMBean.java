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

import java.util.Set;

import jp.ossc.nimbus.core.ServiceBaseMBean;
import jp.ossc.nimbus.core.ServiceName;

/**
 * {@link MessageReceiverService}のMBeanインタフェース<p>
 *
 * @author M.Takata
 * @see MessageReceiverService
 */
public interface MessageReceiverServiceMBean extends ServiceBaseMBean{

    /**
     * {@link MessageListener}へのパラメータオブジェクトのリサイクルリストのデフォルトサイズ。
     */
    public static final int DEFAULT_MESSAGE_LISTENER_PARAMETER_RECYCLE_LIST_SIZE = -1;

    /**
     * {@link ClientConnectionFactory}リモートオブジェクトのJNDI名を設定する。<p>
     *
     * @param name JNDI名
     */
    public void setClientConnectionFactoryJndiName(String name);

    /**
     * {@link ClientConnectionFactory}リモートオブジェクトのJNDI名を取得する。<p>
     *
     * @return JNDI名
     */
    public String getClientConnectionFactoryJndiName();

    /**
     * {@link ClientConnectionFactory}リモートオブジェクトがバインドされている{@link jp.ossc.nimbus.service.repository.Repository Repository}サービスのサービス名を設定する。<p>
     *
     * @param name Repositoryサービスのサービス名
     */
    public void setJndiRepositoryServiceName(ServiceName name);

    /**
     * {@link ClientConnectionFactory}リモートオブジェクトがバインドされている{@link jp.ossc.nimbus.service.repository.Repository Repository}サービスのサービス名を取得する。<p>
     *
     * @return Repositoryサービスのサービス名
     */
    public ServiceName getJndiRepositoryServiceName();

    /**
     * {@link ClientConnectionFactory}サービスのサービス名を設定する。<p>
     *
     * @param name ClientConnectionFactoryサービスのサービス名
     */
    public void setClientConnectionFactoryServiceName(ServiceName name);

    /**
     * {@link ClientConnectionFactory}サービスのサービス名を取得する。<p>
     *
     * @return ClientConnectionFactoryサービスのサービス名
     */
    public ServiceName getClientConnectionFactoryServiceName();

    /**
     * {@link jp.ossc.nimbus.service.queue.Queue Queue}サービスを生成するファクトリサービスのサービス名を設定する。<p>
     * ここで指定されたファクトリサービスから生成されたQueueは、{@link Message}毎に非同期で配信を行う場合の配信Queueとして使用する。<br>
     * 設定しない場合は、内部でQueueが生成される。<br>
     *
     * @param name Queueサービスを生成するファクトリサービスのサービス名
     */
    public void setMessageQueueFactoryServiceName(ServiceName name);

    /**
     * {@link jp.ossc.nimbus.service.queue.Queue Queue}サービスを生成するファクトリサービスのサービス名を取得する。<p>
     *
     * @return Queueサービスを生成するファクトリサービスのサービス名
     */
    public ServiceName getMessageQueueFactoryServiceName();

    /**
     * {@link Message}を分流し、{@link MessageListener}への配信を非同期で行う場合の配信スレッドの数を設定する。<p>
     * デフォルトは0で、各Messageを分流しないで配信する。<br>
     *
     * @param size 配信スレッドの数
     */
    public void setMessageQueueDistributedSize(int size);

    /**
     * {@link Message}を分流し、{@link MessageListener}への配信を非同期で行う場合の配信スレッドの数を取得する。<p>
     *
     * @return 配信スレッドの数
     */
    public int getMessageQueueDistributedSize();

    /**
     * {@link jp.ossc.nimbus.service.queue.Queue Queue}サービスを生成するファクトリサービスのサービス名を設定する。<p>
     * ここで指定されたファクトリサービスから生成されたQueueは、{@link MessageListener}毎に非同期で配信を行う場合の配信Queueとして使用する。<br>
     * 設定しない場合は、内部でQueueが生成される。<br>
     *
     * @param name Queueサービスを生成するファクトリサービスのサービス名
     */
    public void setMessageListenerQueueFactoryServiceName(ServiceName name);

    /**
     * {@link jp.ossc.nimbus.service.queue.Queue Queue}サービスを生成するファクトリサービスのサービス名を取得する。<p>
     *
     * @return Queueサービスを生成するファクトリサービスのサービス名
     */
    public ServiceName getMessageListenerQueueFactoryServiceName();

    /**
     * {@link MessageListener}への配信を非同期で行う場合の配信スレッドの数を設定する。<p>
     * デフォルトは0で、各MessageListenerにシングルスレッドで順次配信する。<br>
     *
     * @param size 配信スレッドの数
     */
    public void setMessageListenerQueueDistributedSize(int size);

    /**
     * {@link MessageListener}への配信を非同期で行う場合の配信スレッドの数を取得する。<p>
     *
     * @return 配信スレッドの数
     */
    public int getMessageListenerQueueDistributedSize();

    /**
     * サービスの開始時に、{@link ClientConnection}を接続するかどうかを設定する。<p>
     *
     * @param isConnect 接続する場合、true
     */
    public void setConnectOnStart(boolean isConnect);

    /**
     * サービスの開始時に、{@link ClientConnection}を接続するかどうかを判定する。<p>
     *
     * @return trueの場合接続する
     */
    public boolean isConnectOnStart();

    /**
     * サービスの開始時に、受信を開始するかどうかを設定する。<p>
     *
     * @param isStart 受信を開始する場合、true
     */
    public void setStartReceiveOnStart(boolean isStart);

    /**
     * サービスの開始時に、受信を開始するかどうかを判定する。<p>
     *
     * @return trueの場合、受信を開始する
     */
    public boolean isStartReceiveOnStart();
    
    /**
     * メッセージ配信の遅延を記録する{@link jp.ossc.nimbus.service.performance.PerformanceRecorder PerformanceRecorder}サービスのサービス名を設定する。<p>
     *
     * @param name PerformanceRecorderサービスのサービス名
     */
    public void setMessageLatencyPerformanceRecorderServiceName(ServiceName name);
    
    /**
     * メッセージ配信の遅延を記録する{@link jp.ossc.nimbus.service.performance.PerformanceRecorder PerformanceRecorder}サービスのサービス名を取得する。<p>
     *
     * @return PerformanceRecorderサービスのサービス名
     */
    public ServiceName getMessageLatencyPerformanceRecorderServiceName();

    /**
     * {@link ClientConnection}を接続する。<p>
     *
     * @exception Exception 接続に失敗した場合
     */
    public void connect() throws Exception;

    /**
     * {@link ClientConnection}を切断する。<p>
     */
    public void close();

    /**
     * 接続しているかどうかを判定する。<p>
     *
     * @return 接続している場合true
     */
    public boolean isConnected();

    /**
     * 受信を開始する。<p>
     *
     * @exception MessageSendException サーバへの要求に失敗した場合
     */
    public void startReceive() throws MessageSendException;

    /**
     * 受信を停止する。<p>
     *
     * @exception MessageSendException サーバへの要求に失敗した場合
     */
    public void stopReceive() throws MessageSendException;

    /**
     * 配信開始しているかどうかを判定する。<br>
     *
     * @return 配信開始している場合true
     */
    public boolean isStartReceive();

    /**
     * 受信中のサブジェクトの集合を取得する。<p>
     *
     * @return 受信中のサブジェクトの集合
     */
    public Set getSubjectNameSet();

    /**
     * 受信件数を取得する。<p>
     *
     * @return 受信件数
     */
    public long getReceiveCount();

    /**
     * 受信件数をリセットする。<p>
     */
    public void resetReceiveCount();

    /**
     * 指定したサブジェクトの受信件数を取得する。<p>
     *
     * @param subject サブジェクト
     * @return 受信件数
     */
    public long getReceiveCount(String subject);

    /**
     * 指定したサブジェクトの受信件数をリセットする。<p>
     *
     * @param subject サブジェクト
     */
    public void resetReceiveCount(String subject);

    /**
     * 登録されているサブジェクトを取得する。<p>
     *
     * @return サブジェクトの集合
     */
    public Set getSubjects();

    /**
     * 指定されたサブジェクトに対して登録されているキーを取得する。<p>
     *
     * @param subject サブジェクト
     * @return キーの集合
     */
    public Set getKeys(String subject);

    /**
     * 登録されている{@link MessageListener}の数を取得する。<p>
     *
     * @return MessageListenerの数
     */
    public int getMessageListenerSize();

    /**
     * {@link Message}を分流し、{@link MessageListener}への配信を非同期で行う場合の非同期処理用のキューの投入件数を取得する。<p>
     *
     * @return 非同期処理用のキューの投入件数
     */
    public long getMessageQueueCount();

    /**
     * {@link Message}を分流し、{@link MessageListener}への配信を非同期で行う場合の非同期処理用のキューの滞留件数を取得する。<p>
     *
     * @return 非同期処理用のキューの滞留件数
     */
    public long getMessageQueueDepth();

    /**
     * {@link Message}を分流し、{@link MessageListener}への配信を非同期で行う場合の非同期処理の平均処理時間[ms]を取得する。<p>
     *
     * @return 非同期処理の平均処理時間[ms]
     */
    public long getMessageQueueAverageHandleProcessTime();

    /**
     * {@link MessageListener}への配信を非同期で行う場合の非同期処理用のキューの投入件数を取得する。<p>
     *
     * @return 非同期処理用のキューの投入件数
     */
    public long getgetMessageListenerQueueCount();

    /**
     * {@link MessageListener}への配信を非同期で行う場合の非同期処理用のキューの滞留件数を取得する。<p>
     *
     * @return 非同期処理用のキューの滞留件数
     */
    public long getMessageListenerQueueDepth();

    /**
     * {@link MessageListener}への配信を非同期で行う場合の非同期処理の平均処理時間[ms]を取得する。<p>
     *
     * @return 非同期処理の平均処理時間[ms]
     */
    public long getMessageListenerQueueAverageHandleProcessTime();

    /**
     * {@link MessageListener}へのパラメータオブジェクトのリサイクルリストの上限サイズを設定する。<p>
     *
     * @param size リサイクルリストの上限サイズ
     */
    public void setMessageListenerParameterRecycleListSize(int size);

    /**
     * {@link MessageListener}へのパラメータオブジェクトのリサイクルリストの上限サイズを取得する。<p>
     *
     * @return リサイクルリストの上限サイズ
     */
    public int getMessageListenerParameterRecycleListSize();

}
