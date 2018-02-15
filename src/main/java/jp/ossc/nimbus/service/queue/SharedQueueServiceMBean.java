/*
 * This software is distributed under following license based on modified BSD
 * style license.
 * ----------------------------------------------------------------------
 * 
 * Copyright 2009 The Nimbus2 Project. All rights reserved.
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
 * policies, either expressed or implied, of the Nimbus2 Project.
 */
package jp.ossc.nimbus.service.queue;

import java.util.*;

import jp.ossc.nimbus.core.*;
import jp.ossc.nimbus.service.context.SharedContextServiceMBean;

/**
 * {@link SharedQueueService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see SharedQueueService
 */
public interface SharedQueueServiceMBean extends SharedContextServiceMBean{
    
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
     * キュー要素を取得しに来た順序どおりに渡す事を保証するかどうかを判定する。<p>
     *
     * @return trueの場合、保証する
     */
    public boolean isSafeGetOrder();
    
    /**
     * キュー要素を取得しに来た順序どおりに渡す事を保証するかどうかを設定する。<p>
     * デフォルトは、falseで保証しない。<br>
     *
     * @param isSafe 保証する場合、true
     */
    public void setSafeGetOrder(boolean isSafe);
    
    /**
     * キュー要素を共有コンテキストに格納する際のキー（{時刻}+{通番}+{UID}）の時刻フォーマットを設定する。<p>
     * デフォルトは、"yyyyMMddHHmmssSSS"。<br>
     *
     * @param format 時刻フォーマット
     */
    public void setSequenceTimestampFormat(String format);
    
    /**
     * キュー要素を共有コンテキストに格納する際のキー（{時刻}+{通番}+{UID}）の時刻フォーマットを取得する。<p>
     *
     * @return 時刻フォーマット
     */
    public String getSequenceTimestampFormat();
    
    /**
     * キュー要素を共有コンテキストに格納する際のキー（{時刻}+{通番}+{UID}）の通番桁数を設定する。<p>
     * デフォルトは、5。<br>
     *
     * @param digit 通番桁数
     */
    public void setSequenceDigit(int digit);
    
    /**
     * キュー要素を共有コンテキストに格納する際のキー（{時刻}+{通番}+{UID}）の通番桁数を取得する。<p>
     *
     * @return 通番桁数
     */
    public int getSequenceDigit();
    
    /**
     * 並列にキューの要素を取得する時に、取得可能な要素を探索する深さを設定する。<p>
     * デフォルトは、2。<br>
     *
     * @param size 探索する深さ
     */
    public void setSeekDepth(int size);
    
    /**
     * 並列にキューの要素を取得する時に、取得可能な要素を探索する深さを取得する。<p>
     *
     * @return 探索する深さ
     */
    public int getSeekDepth();
    
    /**
     * キューの現在の要素リストを取得する。<p>
     * ここで取得されたキュー要素は、この操作ではキューから削除されない。<br>
     *
     * @return キューの現在の要素リスト
     */
    public List elements();
    
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
    
    /**
     * 最大到達時のキューの深さを取得する。<p>
     *
     * @return 最大到達時のキューの深さ
     */
    public long getMaxDepth();
}