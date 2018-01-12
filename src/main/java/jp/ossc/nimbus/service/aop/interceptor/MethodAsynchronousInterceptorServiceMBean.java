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
package jp.ossc.nimbus.service.aop.interceptor;

import jp.ossc.nimbus.core.*;

/**
 * {@link MethodAsynchronousInterceptorService}のMBeanインタフェース<p>
 * 
 * @author M.Takata
 * @see MethodAsynchronousInterceptorService
 */
public interface MethodAsynchronousInterceptorServiceMBean extends ServiceBaseMBean{
    
    /**
     * リクエストを格納する{@link jp.ossc.nimbus.service.queue.Queue Queue}サービスのサービス名を設定する。<p>
     * デフォルトで、{@link jp.ossc.nimbus.service.queue.DefaultQueueService DefaultQueueService}を生成し、使用する。
     *
     * @param name Queueサービスのサービス名
     */
    public void setRequestQueueServiceName(ServiceName name);
    
    /**
     * リクエストを格納する{@link jp.ossc.nimbus.service.queue.Queue Queue}サービスのサービス名を取得する。<p>
     *
     * @return Queueサービスのサービス名
     */
    public ServiceName getRequestQueueServiceName();
    
    /**
     * 非同期呼び出しの戻りを一定時間待つ場合のタイムアウト値[ms]を設定する。<p>
     *
     * @param timeout タイムアウト[ms]
     */
    public void setResponseTimeout(long timeout);
    
    /**
     * 非同期呼び出しの戻りを一定時間待つ場合のタイムアウト値[ms]を取得する。<p>
     *
     * @return タイムアウト[ms]
     */
    public long getResponseTimeout();
    
    /**
     * 非同期呼び出しの戻りを一定時間待って、戻りが返ってこなかった場合に例外をthrowするかどうかを設定する。<p>
     * デフォルトはtrue。
     * 
     * @param isThrow 非同期呼び出しの戻りを一定時間待って戻りが返ってこなかった場合に例外をthrowする場合true
     */
    public void setFailToWaitResponseTimeout(boolean isThrow);
    
    /**
     * 非同期呼び出しの戻りを一定時間待って、戻りが返ってこなかった場合に例外をthrowするかどうかを判定する。<p>
     * 
     * @return 非同期呼び出しの戻りを一定時間待って戻りが返ってこなかった場合に例外をthrowする場合true
     */
    public boolean isFailToWaitResponseTimeout();
    
    /**
     * レスポンスを格納する{@link jp.ossc.nimbus.service.queue.Queue Queue}サービスのサービス名を設定する。<p>
     * {@link #setResponseTimeout(long)}で有効なタイムアウト値が設定されている場合は、デフォルトで{@link jp.ossc.nimbus.service.queue.DefaultQueueService DefaultQueueService}を生成し、使用する。タイムアウト値が設定されていない場合は、デフォルトではレスポンスキューはなしで、レスポンスは捨てられる。<br>
     *
     * @param name Queueサービスのサービス名
     */
    public void setResponseQueueServiceName(ServiceName name);
    
    /**
     * レスポンスを格納する{@link jp.ossc.nimbus.service.queue.Queue Queue}サービスのサービス名を取得する。<p>
     *
     * @return Queueサービスのサービス名
     */
    public ServiceName getResponseQueueServiceName();
    
    /**
     * 非同期処理を行うスレッド数を設定する。<p>
     * デフォルトは、1。
     *
     * @param size スレッド数
     */
    public void setInvokerThreadSize(int size);
    
    /**
     * 非同期処理を行うスレッド数を取得する。<p>
     *
     * @return スレッド数
     */
    public int getInvokerThreadSize();
    
    /**
     * 非同期処理を行うスレッドがデーモンスレッドかどうかを設定する。<p>
     * デフォルトは、true。
     *
     * @param isDaemon デーモンの場合true
     */
    public void setInvokerThreadDaemon(boolean isDaemon);
    
    /**
     * 非同期処理を行うスレッドがデーモンスレッドかどうかを判定する。<p>
     *
     * @return trueの場合デーモン
     */
    public boolean isInvokerThreadDaemon();
    
    /**
     * 非同期処理を実行中のスレッド数を取得する。<p>
     *
     * @return 非同期処理を実行中のスレッド数
     */
    public int getActiveInvokerThreadSize();
    
    /**
     * 呼び出しスレッドの戻り値として、応答キューの戻り値を返すかどうかを設定する。<p>
     * デフォルトは、true。<br>
     *
     * @param isReturn 戻り値を返す場合true
     */
    public void setReturnResponse(boolean isReturn);
    
    /**
     * 呼び出しスレッドの戻り値として、応答キューの戻り値を返すかどうかを判定する。<p>
     *
     * @return trueの場合、戻り値を返す
     */
    public boolean isReturnResponse();
}
