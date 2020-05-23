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

import java.util.Iterator;
import java.util.Collections;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * 待機同期モニタ。<p>
 * Object.wait()には、通知、割り込み、タイムアウトなしに再開される「スプリアスウェイクアップ」という現象が存在する。<br>
 * これは、wait、notifyモデルを使いたいプログラマーにとって、やっかいな問題である。<br>
 * このクラスは、その問題を回避する実装を持った、ユーティリティクラスである。<br>
 * 
 * @author M.Takata
 */
public class WaitSynchronizeMonitor implements SynchronizeMonitor, java.io.Serializable{
    
    private static final long serialVersionUID = -2224847461399411455L;
    
    protected transient Map monitorFlagMap = Collections.synchronizedMap(new LinkedHashMap());
    
    protected boolean isClosed;
    
    /**
     * インスタンスを生成する。<p>
     */
    public WaitSynchronizeMonitor(){
    }
    
    /**
     * 呼び出しスレッドに対するモニタを初期化する。<p>
     * {@link #waitMonitor()}、{@link #waitMonitor(long)}を呼び出す前に、このメソッドを呼ぶ必要がある。<br>
     *
     * @return モニタを初期化する前に通知されていればtrue
     */
    public synchronized boolean initMonitor(){
        return initMonitor(Thread.currentThread());
    }
    
    /**
     * 指定したスレッドに対するモニタを初期化する。<p>
     * 指定したスレッドが、{@link #waitMonitor()}、{@link #waitMonitor(long)}を呼び出す前に、このメソッドを呼ぶ必要がある。<br>
     *
     * @param thread このモニタに対して待機するスレッド
     * @return モニタを初期化する前に通知されていればtrue
     */
    public synchronized boolean initMonitor(Thread thread){
        if(isClosed){
            return true;
        }
        MonitorFlag monitorFlag = (MonitorFlag)monitorFlagMap.get(thread);
        if(monitorFlag == null){
            monitorFlag = new MonitorFlag();
            monitorFlagMap.put(thread, monitorFlag);
        }
        boolean isNotify = monitorFlag.isNotify;
        monitorFlag.isWait = false;
        monitorFlag.isNotify = false;
        return isNotify;
    }
    
    /**
     * 呼び出しスレッドに対するモニタを解放する。<p>
     * 同一スレッドでこのモニタを再利用する場合には、このメソッドを呼び出さなくても良い。<br>
     */
    public synchronized void releaseMonitor(){
        final Thread currentThread = Thread.currentThread();
        monitorFlagMap.remove(currentThread);
        notifyAll();
    }
    
    /**
     * 全てのモニタを解放する。<p>
     */
    public synchronized void releaseAllMonitor(){
        monitorFlagMap.clear();
        notifyAll();
    }
    
    /**
     * 通知が来るまで待機する。<p>
     * {@link #notifyMonitor()}、{@link #notifyAllMonitor()}によって通知されるまで待機する。<br>
     *
     * @exception InterruptedException 割りこまれた場合
     */
    public synchronized void initAndWaitMonitor() throws InterruptedException{
        initAndWaitMonitor(-1);
    }
    
    /**
     * 通知が来るか、指定された時間が経過するまで待機する。<p>
     * {@link #notifyMonitor()}、{@link #notifyAllMonitor()}によって通知されるまで待機する。<br>
     *
     * @return 通知によって起こされた場合true。タイムアウトした場合false
     * @exception InterruptedException 割りこまれた場合
     */
    public synchronized boolean initAndWaitMonitor(long timeout) throws InterruptedException{
        return !initMonitor() ? waitMonitor(timeout) : true;
    }
    
    /**
     * 通知が来るまで待機する。<p>
     * {@link #notifyMonitor()}、{@link #notifyAllMonitor()}によって通知されるまで待機する。<br>
     *
     * @exception InterruptedException 割りこまれた場合
     */
    public synchronized void waitMonitor() throws InterruptedException{
        waitMonitor(-1);
    }
    
    /**
     * 通知が来るか、指定された時間が経過するまで待機する。<p>
     * {@link #notifyMonitor()}、{@link #notifyAllMonitor()}によって通知されるまで待機する。<br>
     *
     * @return 通知によって起こされた場合true。タイムアウトした場合false
     * @exception InterruptedException 割りこまれた場合
     */
    public synchronized boolean waitMonitor(long timeout) throws InterruptedException{
        if(isClosed){
            return true;
        }
        long startTime = System.currentTimeMillis();
        final Thread currentThread = Thread.currentThread();
        MonitorFlag monitorFlag = (MonitorFlag)monitorFlagMap.get(currentThread);
        if(monitorFlag == null){
            return false;
        }
        if(monitorFlag.isNotify){
            return true;
        }
        monitorFlag.isWait = true;
        try{
            long waitTime = timeout;
            while(monitorFlagMap.containsKey(currentThread) && !monitorFlag.isNotify){
                if(timeout > 0){
                    if(waitTime >= 0){
                        wait(waitTime);
                    }
                    waitTime = timeout - (System.currentTimeMillis() - startTime);
                    if(waitTime <= 0){
                        break;
                    }
                }else{
                    wait();
                }
                if(isClosed){
                    return true;
                }
            }
        }finally{
            monitorFlag.isWait = false;
        }
        boolean isNotify = monitorFlag.isNotify;
        monitorFlag.isNotify = false;
        return isNotify && monitorFlagMap.containsKey(currentThread);
    }
    
    /**
     * 待機している最初のスレッドに通知する。<p>
     */
    public synchronized void notifyMonitor(){
        if(monitorFlagMap.size() != 0){
            ((MonitorFlag)monitorFlagMap.values().iterator().next()).isNotify = true;
        }
        notifyAll();
    }
    
    /**
     * 待機している全てのスレッドに通知する。<p>
     */
    public synchronized void notifyAllMonitor(){
        if(monitorFlagMap.size() != 0){
            final Iterator itr = monitorFlagMap.values().iterator();
            while(itr.hasNext()){
                ((MonitorFlag)itr.next()).isNotify = true;
            }
        }
        notifyAll();
    }
    
    /**
     * このスレッドが通知によって起こされたかどうかを判定する。<p>
     * 
     * @return 通知によって起こされた場合はtrue
     */
    public synchronized boolean isNotify(){
        if(isClosed){
            return true;
        }
        final Thread currentThread = Thread.currentThread();
        MonitorFlag monitorFlag = (MonitorFlag)monitorFlagMap.get(currentThread);
        return monitorFlag != null && monitorFlag.isNotify;
    }
    
    /**
     * 最初に待機しているスレッドが現在のスレッドかどうかを判定する。<p>
     * 
     * @return 最初に待機しているスレッドが現在のスレッドである場合はtrue
     */
    public synchronized boolean isFirst(){
        if(monitorFlagMap.size() == 0){
            return false;
        }
        final Thread currentThread = Thread.currentThread();
        if(!monitorFlagMap.containsKey(currentThread)){
            return false;
        }
        Thread first = (Thread)monitorFlagMap.keySet().iterator().next();
        return first == null ? false : first.equals(currentThread);
    }
    
    /**
     * 待機しているスレッドが存在するかどうかを判定する。<p>
     * 
     * @return 待機しているスレッドが存在する場合はtrue
     */
    public synchronized boolean isWait(){
        if(monitorFlagMap.size() != 0){
            final Iterator itr = monitorFlagMap.values().iterator();
            while(itr.hasNext()){
                if(((MonitorFlag)itr.next()).isWait){
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * 待機しているスレッドの数を取得する。<p>
     * 
     * @return 待機しているスレッドの数
     */
    public synchronized int getWaitCount(){
        int count = 0;
        if(monitorFlagMap.size() != 0){
            final Iterator itr = monitorFlagMap.values().iterator();
            while(itr.hasNext()){
                if(((MonitorFlag)itr.next()).isWait){
                    count++;
                }
            }
        }
        return count;
    }
    
    /**
     * 待機しているスレッドを取得する。<p>
     * 
     * @return 待機しているスレッドの配列
     */
    public synchronized Thread[] getWaitThreads(){
        final List result = new ArrayList();
        if(monitorFlagMap.size() != 0){
            final Iterator entries = monitorFlagMap.entrySet().iterator();
            while(entries.hasNext()){
                Map.Entry entry = (Map.Entry)entries.next();
                if(((MonitorFlag)entry.getValue()).isWait){
                    result.add(entry.getKey());
                }
            }
        }
        return (Thread[])result.toArray(new Thread[result.size()]);
    }
    
    public synchronized void close(){
        isClosed = true;
        releaseAllMonitor();
    }
    
    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException{
        in.defaultReadObject();
        monitorFlagMap = Collections.synchronizedMap(new LinkedHashMap());
    }
    
    protected static final class MonitorFlag implements java.io.Serializable{
        private static final long serialVersionUID = -4683612743846239879L;
        public boolean isWait;
        public boolean isNotify;
    }
}