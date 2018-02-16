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
package jp.ossc.nimbus.service.scheduler2;

import jp.ossc.nimbus.core.*;

/**
 * {@link DefaultSchedulerService}のMBeanインタフェース。<p>
 * 
 * @author M.Takata
 */
public interface DefaultSchedulerServiceMBean
 extends AbstractSchedulerServiceMBean{
    
    /**
     * スケジュールを投入する{@link jp.ossc.nimbus.service.queue.Queue Queue}サービスのサービス名を設定する。<p>
     *
     * @param name Queueサービスのサービス名
     */
    public void setQueueServiceName(ServiceName name);
    
    /**
     * スケジュールを投入する{@link jp.ossc.nimbus.service.queue.Queue Queue}サービスのサービス名を取得する。<p>
     *
     * @return Queueサービスのサービス名
     */
    public ServiceName getQueueServiceName();
    
    /**
     * キューを待ち受けるスケジュールディスパッチスレッドの数を設定する。<p>
     * デフォルトは、1。<br>
     *
     * @param size スケジュールディスパッチスレッドの数
     */
    public void setScheduleDispatcherSize(int size);
    
    /**
     * キューを待ち受けるスケジュールディスパッチスレッドの数を取得する。<p>
     *
     * @return スケジュールディスパッチスレッドの数
     */
    public int getScheduleDispatcherSize();
    
    /**
     * キューを待ち受けるスケジュールディスパッチスレッドをデーモンスレッドにするかどうかを設定する。<p>
     * デフォルトは、true。<br>
     *
     * @param isDaemon デーモンスレッドにする場合は、true
     */
    public void setDaemonScheduleDispatcher(boolean isDaemon);
    
    /**
     * キューを待ち受けるスケジュールディスパッチスレッドをデーモンスレッドにするかどうかを判定する。<p>
     *
     * @return trueの場合、デーモンスレッドにする
     */
    public boolean isDaemonScheduleDispatcher();
    
    /**
     * サービスの停止時にスケジュールディスパッチスレッドの終了を待機するタイムアウト[ms]を設定する。<p>
     * タイムアウトした場合は、それ以降のスレッドの終了は、{@link jp.ossc.nimbus.daemon.Daemon#stopNoWait()}を実行する。<br>
     * デフォルトは-1で、全てのスレッドに対して{@link jp.ossc.nimbus.daemon.Daemon#stop()}を実行する。<br>
     *
     * @param timeout タイムアウト[ms]
     */
    public void setStopWaitTimeout(long timeout);
    
    /**
     * サービスの停止時にスケジュールディスパッチスレッドの終了を待機するタイムアウト[ms]を取得する。<p>
     *
     * @return タイムアウト[ms]
     */
    public long getStopWaitTimeout();
    
    /**
     * 現在スケジュール実行中のスレッド数を取得する。<p>
     *
     * @return 現在スケジュール実行中のスレッド数
     */
    public int getActiveScheduleDispatcherSize();
}