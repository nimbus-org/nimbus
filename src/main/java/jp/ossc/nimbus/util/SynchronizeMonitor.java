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
package jp.ossc.nimbus.util;

/**
 * 同期モニタ。<p>
 * 
 * @author M.Takata
 */
public interface SynchronizeMonitor{
    
    /**
     * 呼び出しスレッドに対するモニタを初期化する。<p>
     * {@link #waitMonitor()}、{@link #waitMonitor(long)}を呼び出す前に、このメソッドを呼ぶ必要がある。<br>
     *
     * @return モニタを初期化する前に通知されていればtrue
     */
    public boolean initMonitor();
    
    /**
     * 指定したスレッドに対するモニタを初期化する。<p>
     * 指定したスレッドが、{@link #waitMonitor()}、{@link #waitMonitor(long)}を呼び出す前に、このメソッドを呼ぶ必要がある。<br>
     *
     * @param thread このモニタに対して待機するスレッド
     * @return モニタを初期化する前に通知されていればtrue
     */
    public boolean initMonitor(Thread thread);
    
    /**
     * 呼び出しスレッドに対するモニタを解放する。<p>
     * 同一スレッドでこのモニタを再利用する場合には、このメソッドを呼び出さなくても良い。<br>
     */
    public void releaseMonitor();
    
    /**
     * 全てのモニタを解放する。<p>
     */
    public void releaseAllMonitor();
    
    /**
     * 通知が来るまで待機する。<p>
     * {@link #notifyMonitor()}、{@link #notifyAllMonitor()}によって通知されるまで待機する。<br>
     *
     * @exception InterruptedException 割りこまれた場合
     */
    public void initAndWaitMonitor() throws InterruptedException;
    
    /**
     * 通知が来るか、指定された時間が経過するまで待機する。<p>
     * {@link #notifyMonitor()}、{@link #notifyAllMonitor()}によって通知されるまで待機する。<br>
     *
     * @return 通知によって起こされた場合true。タイムアウトした場合false
     * @exception InterruptedException 割りこまれた場合
     */
    public boolean initAndWaitMonitor(long timeout) throws InterruptedException;
    
    /**
     * 通知が来るまで待機する。<p>
     * {@link #notifyMonitor()}、{@link #notifyAllMonitor()}によって通知されるまで待機する。<br>
     *
     * @exception InterruptedException 割りこまれた場合
     */
    public void waitMonitor() throws InterruptedException;
    
    /**
     * 通知が来るか、指定された時間が経過するまで待機する。<p>
     * {@link #notifyMonitor()}、{@link #notifyAllMonitor()}によって通知されるまで待機する。<br>
     *
     * @return 通知によって起こされた場合true。タイムアウトした場合false
     * @exception InterruptedException 割りこまれた場合
     */
    public boolean waitMonitor(long timeout) throws InterruptedException;
    
    /**
     * 待機している最初のスレッドに通知する。<p>
     */
    public void notifyMonitor();
    
    /**
     * 待機している全てのスレッドに通知する。<p>
     */
    public void notifyAllMonitor();
    
    /**
     * このスレッドが通知によって起こされたかどうかを判定する。<p>
     * 
     * @return 通知によって起こされた場合はtrue
     */
    public boolean isNotify();
    
    /**
     * 最初に待機しているスレッドが現在のスレッドかどうかを判定する。<p>
     * 
     * @return 最初に待機しているスレッドが現在のスレッドである場合はtrue
     */
    public boolean isFirst();
    
    /**
     * 待機しているスレッドが存在するかどうかを判定する。<p>
     * 
     * @return 待機しているスレッドが存在する場合はtrue
     */
    public boolean isWait();
    
    /**
     * 待機しているスレッドの数を取得する。<p>
     * 
     * @return 待機しているスレッドの数
     */
    public int getWaitCount();
    
    /**
     * 待機しているスレッドを取得する。<p>
     * 
     * @return 待機しているスレッドの配列
     */
    public Thread[] getWaitThreads();
    
    /**
     * 終了する。<p>
     * 待機している全てのスレッドに通知し、このモニタを無効にする。<br>
     */
    public void close();
}