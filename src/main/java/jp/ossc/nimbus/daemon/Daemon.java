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
package jp.ossc.nimbus.daemon;

import jp.ossc.nimbus.util.*;

/**
 * デーモンスレッド。<p>
 * デーモンスレッドの安全な制御をめざしたものです。<br>
 *
 * @author H.Nakano
 */
public class Daemon implements Runnable, DaemonControl{
    
    //## クラスメンバー変数宣言 ##
    
    /**
     * デーモン稼動中判定フラグ。<p>
     */
    protected volatile boolean isRunning;
    
    /**
     * ブロッキング状態判定フラグ。<p>
     */
    protected volatile boolean isBlocking;
    
    /**
     * サスペンド状態判定フラグ。<p>
     */
    protected volatile boolean isSusupend;
    protected SynchronizeMonitor susupendMonitor = new WaitSynchronizeMonitor();
    
    /**
     * デーモンスレッドオブジェクト。<p>
     */
    protected transient Thread daemonThread;
    
    /**
     * デーモンスレッド名。<p>
     */
    protected String threadName;
    
    /**
     * デーモン設定フラグ。<p>
     * デフォルトは、true。
     */
    protected boolean isDaemon = true;
    
    /**
     * デーモンランナブル。<p>
     */
    protected DaemonRunnable runnable;
    
    /**
     * ガベージ中フラグ。<p>
     */
    protected boolean isGarbaging;
    
    /**
     * 消費中フラグ。<p>
     */
    protected boolean isConsuming;
    
    /**
     * 供給中フラグ。<p>
     */
    protected boolean isProviding;
    
    /**
     * 優先順位。<p>
     */
    protected int priority = -1;
    
    protected long suspendCheckInterval = 500l;
    
    protected long lastProvideTime = -1;
    
    /**
     * インスタンスを生成する。<p>
     *
     * @param run デーモン処理を実行するDaemonRunnable
     */
    public Daemon(DaemonRunnable run){
        runnable = run;
    }
    
    /**
     * {@link DaemonRunnable}を取得する。<p>
     * 
     * @return DaemonRunnable
     */
    public DaemonRunnable getDaemonRunnable(){
        return runnable;
    }
    
    /**
     * デーモンスレッドを取得する。<p>
     *
     * @return デーモンスレッド
     */
    public Thread getDaemonThread(){
        return daemonThread;
    }
    
    /**
     * デーモンスレッドの名前を設定する。<p>
     * 
     * @param name デーモンスレッドの名前
     */
    public void setName(String name){
        threadName = name;
        if(daemonThread != null){
            daemonThread.setName(name);
        }
    }
    
    /**
     * デーモンスレッドの名前を取得する。<p>
     * 
     * @return デーモンスレッドの名前
     */
    public String getName(){
        return threadName;
    }
    
    /**
     * デーモンスレッドの優先順位を設定する。<p>
     * 
     * @param newPriority デーモンスレッドの優先順位
     */
    public void setPriority(int newPriority){
        priority = newPriority;
        if(daemonThread != null){
            daemonThread.setPriority(newPriority);
        }
    }
    
    /**
     * デーモンスレッドの優先順位を取得する。<p>
     * 
     * @return デーモンスレッドの優先順位
     */
    public int getPriority(){
        return daemonThread == null ? priority : daemonThread.getPriority();
    }
    
    /**
     * 一時停止中から復帰するべきかをチェックする間隔[ms]を設定する。<p>
     * デフォルトは、500[ms]。<br>
     *
     * @param interval チェック間隔[ms]
     */
    public void setSuspendCheckInterval(long interval){
        suspendCheckInterval = interval;
    }
    
    /**
     * 一時停止中から復帰するべきかをチェックする間隔[ms]を取得する。<p>
     *
     * @return チェック間隔[ms]
     */
    public long getSuspendCheckInterval(){
        return suspendCheckInterval;
    }
    
    /**
     * 稼動状態を判定する。<p>
     * 
     * @return trueの場合、稼動中
     */
    public boolean isRunning(){
        return this.isRunning;
    }
    
    /**
     * 稼動状態を設定する。<p>
     * 
     * @param runFlg 稼動中に設定したい場合true
     */
    public void setRunning(boolean runFlg){
        this.isRunning = runFlg;
    }
    
    /**
     * ブロッキング状態を判定する。<p>
     *
     * @return trueの場合、ブロック中
     */
    public boolean isBlocking(){
        return this.isBlocking;
    }
    
    /**
     * ブロッキング状態を設定する。<p>
     *
     * @param blockFlg ブロック中に設定したい場合true
     */
    public void setBlocking(boolean blockFlg){
        this.isBlocking = blockFlg;
    }
    
    /**
     * デーモンフラグを設定する。<p>
     * デフォルトでは、true。
     *
     * @param isDaemon デーモンスレッドにする場合true
     */
    public void setDaemon(boolean isDaemon){
        this.isDaemon = isDaemon;
    }
    
    /**
     * デーモンスレッドかどうか判定する。<p>
     *
     * @return trueの場合、デーモンスレッド
     */
    public boolean isDaemon(){
        return isDaemon ;
    }
    
    /**
     * 供給中かどうかを判定する。<p>
     *
     * @return 供給中の場合true
     */
    public boolean isProviding(){
        return isProviding;
    }
    
    /**
     * 消費中かどうかを判定する。<p>
     *
     * @return 消費中の場合true
     */
    public boolean isConsuming(){
        return isConsuming;
    }
    
    /**
     * 一時停止中かどうかを判定する。<p>
     *
     * @return 一時停止中の場合true
     */
    public boolean isSusupend(){
        return isSusupend;
    }
    
    public void sleep(long interval, boolean isFirstSleep) throws InterruptedException{
        if(lastProvideTime == -1){
            if(isFirstSleep){
                Thread.sleep(interval);
            }
        }else{
            final long currentTime = System.currentTimeMillis();
            final long sleepTime = interval - (lastProvideTime - currentTime);
            if(sleepTime > 0){
                Thread.sleep(sleepTime);
            }
        }
    }
    
    /**
     * このデーモンスレッドの状態を取得する。<p>
     *
     * @return デーモンスレッドの状態
     */
    public CsvArrayList getDeamonInfo(){
        final CsvArrayList parser = new CsvArrayList();
        
        //デーモンスレッド名
        parser.add(getName());
        
        //ランニング状態
        parser.add(String.valueOf(isRunning()));
        
        //ブロッキング状態
        parser.add(String.valueOf(isBlocking()));
        
        return parser;
    }
    
    /**
     * スレッドを開始する。<p>
     */
    public synchronized void start(){
        // すでに実行中ならリターン
        if(isRunning()){
            return;
        }else if(!runnable.onStart()){
            return;
        }
        // 新しいスレッドを作成する
        if(getName() == null || getName().length() == 0){
            daemonThread = new Thread(this);
        }else{
            daemonThread = new Thread(this, getName());
        }
        daemonThread.setDaemon(isDaemon());
        if(priority > 0){
            daemonThread.setPriority(priority);
        }
        
        // 実行中フラグ設定
        setRunning(true);
        setBlocking(true);
        if(isSusupend){
            susupendMonitor.initMonitor(daemonThread);
        }
        
        daemonThread.start();
    }
    
    /**
     * スレッドを停止する。<p>
     * スレッドが停止するまで、60秒だけ待機する。
     */
    public synchronized void stop(){
        stop(60000);
    }
    
    /**
     * スレッドを停止する。<p>
     * スレッドが停止するまで、指定された時間だけ待機する。
     *
     * @param millis 待機時間[ms]
     */
    public synchronized void stop(long millis){
        if(daemonThread == null){
            // デーモンは停止中
            return;
        }else if(!runnable.onStop()){
            return;
        }
        
        setRunning(false);
        if(isBlocking() && daemonThread != null && daemonThread.isAlive()){
            isSusupend = false;
        }
        if(daemonThread != null){
            daemonThread.interrupt();
            
            if(isConsuming){
                if(!daemonThread.isInterrupted()){
                    daemonThread.interrupt();
                }
            }
        }
        if(millis >= 0){
            stopWait(millis);
        }
    }
    
    /**
     * スレッドが停止するまで待つ。<p>
     */
    public synchronized void stopWait(){
        stopWait(0);
    }
    
    /**
     * スレッドが停止するまで待つ。<p>
     */
    public synchronized void stopWait(long millis){
        long startTime = System.currentTimeMillis();
        try{
            if(daemonThread != null && daemonThread.isAlive()){
                daemonThread.join(millis);
            }
        }catch(InterruptedException e){
            Thread.interrupted();
            long processTime = System.currentTimeMillis() - startTime;
            if(millis > processTime){
                stopWait(millis - processTime);
            }
        }
        daemonThread = null;
    }
    
    /**
     * スレッドに停止命令を出す。<p>
     */
    public synchronized void stopNoWait(){
        stop(-1);
    }
    
    /**
     * スレッドを一時停止する。<p>
     */
    public synchronized void suspend(){
        if(isSusupend){
            // デーモンは停止中
            return;
        }else if(!runnable.onSuspend()){
            return;
        }
        isSusupend = true;
        if(daemonThread != null){
            susupendMonitor.initMonitor(daemonThread);
        }
    }
    
    /**
     * スレッドを再開する。<p>
     */
    public synchronized void resume(){
        if(!isSusupend){
            // デーモンは停止中
            return;
        }else if(!runnable.onResume()){
            return;
        }
        isSusupend = false;
        susupendMonitor.notifyMonitor();
    }
    
    public long getLastProvideTime(){
        return lastProvideTime;
    }
    
    /**
     * デーモンスレッドを実行する。<p>
     */
    public void run(){
        boolean breakFlg = false;
        Object waitObj = null;
        try{
            //ループは以下の２つの変数を制御すること。
            while(isRunning()){
                setBlocking(true);
                // 何らかのアクションを待つ場合はInterruptedException
                // をキャッチすること
                while(isSusupend){
                    try{
                        susupendMonitor.waitMonitor(suspendCheckInterval);
                    }catch(InterruptedException e1){
                        Thread.interrupted();
                        breakFlg = true;
                        break;
                    }
                }
                if(breakFlg){
                    breakFlg = false;
                    continue;
                }
                isProviding = true;
                try{
                    lastProvideTime = System.currentTimeMillis();
                    waitObj = runnable.provide(this);
                }catch(InterruptedException e){
                    if(!breakFlg && isRunning()){
                        e.printStackTrace();
                    }else{
                        break;
                    }
                }catch(Throwable e){
                    if(!breakFlg && isRunning()){
                        e.printStackTrace();
                    }
                    Thread.interrupted();
                    waitObj = null;
                }
                isProviding = false;
                while(this.isSusupend){
                    try{
                        susupendMonitor.waitMonitor(suspendCheckInterval);
                    }catch(InterruptedException e2){
                        Thread.interrupted();
                        breakFlg= true;
                        break;
                    }
                }
                setBlocking(false);
                if(breakFlg){
                    breakFlg = false;
                    continue;
                }
                // 応答処理
                isConsuming = true;
                try{
                    runnable.consume(waitObj, this);
                }catch(InterruptedException e){
                    if(!breakFlg && isRunning()){
                        e.printStackTrace();
                    }
                }catch(Throwable e){
                    if(!breakFlg && isRunning()){
                        e.printStackTrace();
                    }
                }
                isConsuming = false;
            }
        }finally{
            // 終了時はキューの残りを書き出す
            setRunning(false);
            isGarbaging = true;
            try{
                runnable.garbage();
            }catch(Throwable e){
                e.printStackTrace();
            }
            isGarbaging = false;
        }
    }
}
