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

import java.util.*;

import jp.ossc.nimbus.core.*;

/**
 * {@link DistributedQueueService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see DistributedQueueService
 */
public interface DistributedQueueServiceMBean extends ServiceBaseMBean{
    
    public void setDistributedQueueSelectorServiceName(ServiceName name);
    
    public ServiceName getDistributedQueueSelectorServiceName();
    
    /**
     * キューに対して無限取得待ちをするスレッドがsleepする時間を設定する。<p>
     * 自分がキュー待ちの先頭でない場合や、キューに溜まってない場合は、再びsleepする。<br>
     * デフォルトは、10秒。
     *
     * @param millis キューに対して無限取得待ちをするスレッドがsleepする時間[ms]
     */
    public void setSleepTime(long millis);
    
    /**
     * キューに対して無限取得待ちをするスレッドがsleepする時間を取得する。<p>
     *
     * @return キューに対して無限取得待ちをするスレッドがsleepする時間[ms]
     */
    public long getSleepTime();
    
    /**
     * キューの最大閾値を設定する。<p>
     * キューの深さが最大閾値に到達すると、キューへの投入は待たされ、キューからの引き抜きと同期される。<br>
     * デフォルトは、-1で、最大閾値なしの状態である。<br>
     *
     * @param size キューの最大閾値
     */
    public void setMaxThresholdSize(int size);
    
    /**
     * キューの最大閾値を取得する。<p>
     *
     * @return キューの最大閾値
     */
    public int getMaxThresholdSize();
    
    /**
     * 内部で使用する{@link jp.ossc.nimbus.util.SynchronizeMonitor SynchronizeMonitor}の実装クラスを設定する。<p>
     *
     * @param clazz SynchronizeMonitorの実装クラス
     */
    public void setSynchronizeMonitorClass(Class clazz);
    
    /**
     * 内部で使用する{@link jp.ossc.nimbus.util.SynchronizeMonitor SynchronizeMonitor}の実装クラスを取得する。<p>
     *
     * @return SynchronizeMonitorの実装クラス
     */
    public Class getSynchronizeMonitorClass();
    
    /**
     * キューを初期化する。 <p>
     */
    public void clear();
    
    /**
     * これまでにキューに格納された数を取得する。<p>
     *
     * @return これまでにキューに格納された数
     */
    public long getCount();
    
    /**
     * 前回問い合わせからキューに格納された数を取得する。<p>
     *
     * @return 前回問い合わせからキューに格納された数
     */
    public long getCountDelta();
    
    /**
     * 最後にキューに格納された時刻を取得する。<p>
     *
     * @return 最後にキューに格納された時刻
     */
    public long getLastPushedTimeMillis();
    
    /**
     * 最後にキューに格納された時刻を取得する。<p>
     *
     * @return 最後にキューに格納された時刻
     */
    public Date getLastPushedTime();
    
    /**
     * 現在のキューの深さを取得する。<p>
     *
     * @return 現在のキューの深さ
     */
    public long getDepth();
    
    /**
     * 前回問い合わせからのキューの深さを取得する。<p>
     *
     * @return 前回問い合わせからのキューの深さ
     */
    public long getDepthDelta();
}
