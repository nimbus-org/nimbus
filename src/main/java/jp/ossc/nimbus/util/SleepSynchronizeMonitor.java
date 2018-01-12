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

import java.util.Collections;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Sleep同期モニタ。<p>
 * Thread.sleep(long)を使った、{@link SynchronizeMonitor}実装クラス。<br>
 * 
 * @author M.Takata
 */
public class SleepSynchronizeMonitor implements SynchronizeMonitor, java.io.Serializable{
    
    private static final long serialVersionUID = -842881189032766657L;
    
    private long infiniteWaitInterval = 1000;
    
    protected transient Map monitorFlagMap = Collections.synchronizedMap(new LinkedHashMap());
    
    protected boolean isClosed;
    
    /**
     * インスタンスを生成する。<p>
     */
    public SleepSynchronizeMonitor(){
    }
    
    public void setInfiniteWaitInterval(long interval){
        infiniteWaitInterval = interval;
    }
    public long getInfiniteWaitInterval(){
        return infiniteWaitInterval;
    }
    
    /**
     * 呼び出しスレッドに対するモニタを初期化する。<p>
     * {@link #waitMonitor()}、{@link #waitMonitor(long)}を呼び出す前に、このメソッドを呼ぶ必要がある。<br>
     *
     * @return モニタを初期化する前に通知されていればtrue
     */
    public boolean initMonitor(){
        return initMonitor(Thread.currentThread());
    }
    
    /**
     * 指定したスレッドに対するモニタを初期化する。<p>
     * 指定したスレッドが、{@link #waitMonitor()}、{@link #waitMonitor(long)}を呼び出す前に、このメソッドを呼ぶ必要がある。<br>
     *
     * @param thread このモニタに対して待機するスレッド
     * @return モニタを初期化する前に通知されていればtrue
     */
    public boolean initMonitor(Thread thread){
        if(isClosed){
            return true;
        }
        MonitorFlag monitorFlag = (MonitorFlag)monitorFlagMap.get(thread);
        if(monitorFlag == null){
            monitorFlag = new MonitorFlag();
            synchronized(monitorFlagMap){
                monitorFlagMap.put(thread, monitorFlag);
            }
        }
        synchronized(monitorFlag){
            boolean isNotify = monitorFlag.isNotify;
            monitorFlag.isWait = false;
            monitorFlag.isNotify = false;
            return isNotify;
        }
    }
    
    /**
     * 呼び出しスレッドに対するモニタを解放する。<p>
     * 同一スレッドでこのモニタを再利用する場合には、このメソッドを呼び出さなくても良い。<br>
     */
    public void releaseMonitor(){
        final Thread currentThread = Thread.currentThread();
        releaseMonitor(currentThread);
    }
    
    private void releaseMonitor(Thread currentThread){
        MonitorFlag monitorFlag = null;
        synchronized(monitorFlagMap){
            monitorFlag = (MonitorFlag)monitorFlagMap.get(currentThread);
            monitorFlagMap.remove(currentThread);
        }
        if(monitorFlag != null){
            synchronized(monitorFlag){
                monitorFlag.isWait = false;
                monitorFlag.isNotify = false;
            }
        }
    }
    
    /**
     * 全てのモニタを解放する。<p>
     */
    public void releaseAllMonitor(){
        synchronized(monitorFlagMap){
            Object[] threads = monitorFlagMap.keySet().toArray();
            for(int i = 0; i < threads.length; i++){
                releaseMonitor((Thread)threads[i]);
            }
        }
    }
    
    /**
     * 通知が来るまで待機する。<p>
     * {@link #notifyMonitor()}、{@link #notifyAllMonitor()}によって通知されるまで待機する。<br>
     *
     * @exception InterruptedException 割りこまれた場合
     */
    public void initAndWaitMonitor() throws InterruptedException{
        initAndWaitMonitor(-1);
    }
    
    /**
     * 通知が来るか、指定された時間が経過するまで待機する。<p>
     * {@link #notifyMonitor()}、{@link #notifyAllMonitor()}によって通知されるまで待機する。<br>
     *
     * @return 通知によって起こされた場合true。タイムアウトした場合false
     * @exception InterruptedException 割りこまれた場合
     */
    public boolean initAndWaitMonitor(long timeout) throws InterruptedException{
        return !initMonitor() ? waitMonitor(timeout) : true;
    }
    
    /**
     * 通知が来るまで待機する。<p>
     * {@link #notifyMonitor()}、{@link #notifyAllMonitor()}によって通知されるまで待機する。<br>
     *
     * @exception InterruptedException 割りこまれた場合
     */
    public void waitMonitor() throws InterruptedException{
        waitMonitor(-1);
    }
    
    /**
     * 通知が来るか、指定された時間が経過するまで待機する。<p>
     * {@link #notifyMonitor()}、{@link #notifyAllMonitor()}によって通知されるまで待機する。<br>
     *
     * @return 通知によって起こされた場合true。タイムアウトした場合false
     * @exception InterruptedException 割りこまれた場合
     */
    public boolean waitMonitor(long timeout) throws InterruptedException{
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
        synchronized(monitorFlag){
            monitorFlag.isWait = true;
        }
        try{
            long waitTime = timeout;
            while(!monitorFlag.isNotify){
                if(timeout > 0){
                    if(waitTime >= 0){
                        try{
                            Thread.sleep(waitTime);
                        }catch(InterruptedException e){
                            if(!monitorFlag.isNotify){
                                throw e;
                            }
                        }
                    }
                    waitTime = timeout - (System.currentTimeMillis() - startTime);
                    if(waitTime <= 0){
                        break;
                    }
                }else{
                    try{
                        Thread.sleep(infiniteWaitInterval);
                    }catch(InterruptedException e){
                        if(!monitorFlag.isNotify){
                            throw e;
                        }
                    }
                }
                if(isClosed){
                    return true;
                }
            }
        }finally{
            synchronized(monitorFlag){
                monitorFlag.isWait = false;
                Thread.interrupted();
            }
        }
        synchronized(monitorFlag){
            boolean isNotify = monitorFlag.isNotify;
            monitorFlag.isNotify = false;
            return isNotify;
        }
    }
    
    /**
     * 待機している最初のスレッドに通知する。<p>
     */
    public void notifyMonitor(){
        if(monitorFlagMap.size() != 0){
            Map.Entry entry = null;
            synchronized(monitorFlagMap){
                if(monitorFlagMap.size() != 0){
                    entry = (Map.Entry)monitorFlagMap.entrySet().iterator().next();
                }
            }
            if(entry != null){
                MonitorFlag monitorFlag = (MonitorFlag)entry.getValue();
                synchronized(monitorFlag){
                    monitorFlag.isNotify = true;
                    if(monitorFlag.isWait){
                        ((Thread)entry.getKey()).interrupt();
                    }
                }
            }
        }
    }
    
    /**
     * 待機している全てのスレッドに通知する。<p>
     */
    public void notifyAllMonitor(){
        if(monitorFlagMap.size() != 0){
            Object[] entries = null;
            synchronized(monitorFlagMap){
                if(monitorFlagMap.size() != 0){
                    entries = monitorFlagMap.entrySet().toArray();
                }
            }
            if(entries != null){
                for(int i = 0; i < entries.length; i++){
                    Map.Entry entry = (Map.Entry)entries[i];
                    MonitorFlag monitorFlag = (MonitorFlag)entry.getValue();
                    synchronized(monitorFlag){
                        monitorFlag.isNotify = true;
                        if(monitorFlag.isWait){
                            ((Thread)entry.getKey()).interrupt();
                        }
                    }
                }
            }
        }
    }
    
    /**
     * このスレッドが通知によって起こされたかどうかを判定する。<p>
     * 
     * @return 通知によって起こされた場合はtrue
     */
    public boolean isNotify(){
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
    public boolean isFirst(){
        if(monitorFlagMap.size() == 0){
            return false;
        }
        final Thread currentThread = Thread.currentThread();
        if(!monitorFlagMap.containsKey(currentThread)){
            return false;
        }
        Thread first = null;
        synchronized(monitorFlagMap){
            if(monitorFlagMap.size() != 0){
                first = (Thread)monitorFlagMap.keySet().iterator().next();
            }
        }
        return first == null ? false : first.equals(currentThread);
    }
    
    /**
     * 待機しているスレッドが存在するかどうかを判定する。<p>
     * 
     * @return 待機しているスレッドが存在する場合はtrue
     */
    public boolean isWait(){
        if(monitorFlagMap.size() != 0){
            Object[] flags = null;
            synchronized(monitorFlagMap){
                if(monitorFlagMap.size() != 0){
                    flags = monitorFlagMap.values().toArray();
                }
            }
            if(flags != null){
                for(int i = 0; i < flags.length; i++){
                    if(((MonitorFlag)flags[i]).isWait){
                        return true;
                    }
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
    public int getWaitCount(){
        int count = 0;
        if(monitorFlagMap.size() != 0){
            Object[] flags = null;
            synchronized(monitorFlagMap){
                if(monitorFlagMap.size() != 0){
                    flags = monitorFlagMap.values().toArray();
                }
            }
            if(flags != null){
                for(int i = 0; i < flags.length; i++){
                    if(((MonitorFlag)flags[i]).isWait){
                        count++;
                    }
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
    public Thread[] getWaitThreads(){
        final List result = new ArrayList();
        if(monitorFlagMap.size() != 0){
            Object[] entries = null;
            synchronized(monitorFlagMap){
                if(monitorFlagMap.size() != 0){
                    entries = monitorFlagMap.entrySet().toArray();
                }
            }
            if(entries != null){
                for(int i = 0; i < entries.length; i++){
                    Map.Entry entry = (Map.Entry)entries[i];
                    if(((MonitorFlag)entry.getValue()).isWait){
                        result.add(entry.getKey());
                    }
                }
            }
        }
        return (Thread[])result.toArray(new Thread[result.size()]);
    }
    
    public void close(){
        isClosed = true;
        notifyAllMonitor();
    }
    
    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException{
        in.defaultReadObject();
        monitorFlagMap = Collections.synchronizedMap(new LinkedHashMap());
    }
    
    protected static final class MonitorFlag implements java.io.Serializable{
        private static final long serialVersionUID = 3458888948264232416L;
        public volatile boolean isWait;
        public volatile boolean isNotify;
    }
}