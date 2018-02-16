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
package jp.ossc.nimbus.service.queue;

import jp.ossc.nimbus.core.*;

/**
 * {@link DistributedQueueHandlerContainerService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see DistributedQueueHandlerContainerService
 */
public interface DistributedQueueHandlerContainerServiceMBean extends ServiceBaseMBean{
    
    public static final String DEFAULT_HANDLING_ERROR_MESSAGE_ID = "QHC__00001";
    public static final String DEFAULT_RETRY_OVER_ERROR_MESSAGE_ID = "QHC__00002";
    
    public void setDistributedQueueSelectorServiceName(ServiceName name);
    public ServiceName getDistributedQueueSelectorServiceName();
    
    /**
     * {@link QueueHandler}サービスのサービス名を設定する。<p>
     *
     * @param name QueueHandlerサービスのサービス名
     */
    public void setQueueHandlerServiceName(ServiceName name);
    
    /**
     * {@link QueueHandler}サービスのサービス名を取得する。<p>
     *
     * @return QueueHandlerサービスのサービス名
     */
    public ServiceName getQueueHandlerServiceName();
    
    /**
     * サービスの停止時に{@link Queue#release()}を呼び出し、Queueを開放するかどうかを設定する。<p>
     * デフォルトは、true。１つのQueueを複数のコンテナで参照する場合は、falseにするべきである。<br>
     *
     * @param isRelease 開放する場合はtrue
     */
    public void setReleaseQueue(boolean isRelease);
    
    /**
     * サービスの停止時に{@link Queue#release()}を呼び出し、Queueを開放するかどうかを判定する。<p>
     *
     * @return trueの場合は、開放する
     */
    public boolean isReleaseQueue();
    
    /**
     * キューを待ち受ける最大時間[ms]を設定する。<p>
     * タイムアウトした場合は、{@link QueueHandler#handleDequeuedObject(Object)}にnullを渡す。<br>
     *
     * @param timeout キューを待ち受ける最大時間[ms]
     */
    public void setWaitTimeout(long timeout);
    
    /**
     * キューを待ち受ける最大時間[ms]を取得する。<p>
     *
     * @return キューを待ち受ける最大時間[ms]
     */
    public long getWaitTimeout();
    
    /**
     * ハンドリングの際にエラーが発生した場合にリトライする回数を設定する。<p>
     * デフォルトは、0でリトライしない。<br>
     *
     * @param count リトライ回数
     */
    public void setMaxRetryCount(int count);
    
    /**
     * ハンドリングの際にエラーが発生した場合にリトライする回数を取得する。<p>
     *
     * @return リトライ回数
     */
    public int getMaxRetryCount();
    
    /**
     * ハンドリングの際にエラーが発生した場合にリトライする間隔を設定する。<p>
     * デフォルトは、1000[ms]。<br>
     *
     * @param interval リトライ間隔
     */
    public void setRetryInterval(long interval);
    
    /**
     * ハンドリングの際にエラーが発生した場合にリトライする間隔を取得する。<p>
     *
     * @return リトライ間隔
     */
    public long getRetryInterval();
    
    /**
     * ハンドリングの際にエラーが発生した事を通知するログのメッセージIDを設定する。<p>
     * デフォルトは、{@link #DEFAULT_HANDLING_ERROR_MESSAGE_ID}。<br>
     *
     * @param id ログのメッセージID
     */
    public void setHandlingErrorMessageId(String id);
    
    /**
     * ハンドリングの際にエラーが発生した事を通知するログのメッセージIDを取得する。<p>
     *
     * @return ログのメッセージID
     */
    public String getHandlingErrorMessageId();
    
    /**
     * ハンドリングの際にエラーが発生し、リトライ回数を越えた事を通知するログのメッセージIDを設定する。<p>
     * デフォルトは、{@link #DEFAULT_RETRY_OVER_ERROR_MESSAGE_ID}。<br>
     *
     * @param id ログのメッセージID
     */
    public void setRetryOverErrorMessageId(String id);
    
    /**
     * ハンドリングの際にエラーが発生し、リトライ回数を越えた事を通知するログのメッセージIDを取得する。<p>
     *
     * @return ログのメッセージID
     */
    public String getRetryOverErrorMessageId();
    
    /**
     * キューを待ち受ける{@link QueueHandler}スレッドの数を取得する。<p>
     *
     * @return QueueHandlerスレッドの数
     */
    public int getQueueHandlerSize();
    
    /**
     * 現在ハンドリング中のスレッド数を取得する。<p>
     *
     * @return 現在ハンドリング中のスレッド数
     */
    public int getActiveQueueHandlerSize();
    
    /**
     * 現在待機中のスレッド数を取得する。<p>
     *
     * @return 現在待機中のスレッド数
     */
    public int getStandbyQueueHandlerSize();
    
    /**
     * キューを待ち受ける{@link QueueHandler}スレッドをデーモンスレッドにするかどうかを設定する。<p>
     * デフォルトは、true。<br>
     *
     * @param isDaemon デーモンスレッドにする場合は、true
     */
    public void setDaemonQueueHandler(boolean isDaemon);
    
    /**
     * キューを待ち受ける{@link QueueHandler}スレッドをデーモンスレッドにするかどうかを判定する。<p>
     *
     * @return trueの場合、デーモンスレッドにする
     */
    public boolean isDaemonQueueHandler();
    
    /**
     * キューを待ち受ける{@link QueueHandler}スレッドの優先順位を設定する。<p>
     * デフォルトは、-1で設定しない。<br>
     *
     * @param newPriority スレッドの優先順位
     */
    public void setQueueHandlerThreadPriority(int newPriority);
    
    /**
     * キューを待ち受ける{@link QueueHandler}スレッドの優先順位を取得する。<p>
     *
     * @return スレッドの優先順位
     */
    public int getQueueHandlerThreadPriority();
    
    /**
     * {@link Queue}から取得した要素がnullの場合に無視するかどうかを設定する。<p>
     * デフォルトは、falseで無視しない。<br>
     *
     * @param isIgnore 無視する場合true
     */
    public void setIgnoreNullElement(boolean isIgnore);
    
    /**
     * {@link Queue}から取得した要素がnullの場合に無視するかどうかを判定する。<p>
     *
     * @return trueの場合、無視する
     */
    public boolean isIgnoreNullElement();
    
    /**
     * キューを初期化する。<p>
     */
    public void clear();
    
    /**
     * キューサイズを取得する。<p>
     * 
     * @return キュー格納件数
     */
    public int size();
    
    /**
     * キューに投入された件数を取得する。<p>
     *
     * @return キュー投入件数
     */
    public long getCount();
    
    /**
     * 現在のキューの深さを取得する。<p>
     *
     * @return 現在のキューの深さ
     */
    public long getDepth();
    
    /**
     * キューから引き抜いた後の処理時間の平均を取得する。<p>
     *
     * @return 平均処理時間[ms]
     */
    public long getAverageHandleProcessTime();
    
    /**
     * 再開する。<p>
     */
    public void resume();
    
    /**
     * 中断する。<p>
     */
    public void suspend();
    
    /**
     * 中断しているかどうかを判定する。<p>
     *
     * @return 中断している場合、true
     */
    public boolean isSuspend();
}