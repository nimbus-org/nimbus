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
package jp.ossc.nimbus.service.semaphore;

import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;

import jp.ossc.nimbus.util.SynchronizeMonitor;
import jp.ossc.nimbus.util.WaitSynchronizeMonitor;

/**
 * メモリセマフォ。<p>
 *
 * @author H.Nakano
 */
public class MemorySemaphore implements Semaphore, java.io.Serializable{
    
    private static final long serialVersionUID = -408553618405283847L;
    
    //## メンバー変数宣言 ##
    
    /** セマフォリソース数 */
    protected volatile int mResourceCnt = -1;
    
    /** セマフォ初期化数 */
    protected volatile int mInitialResource = -1;
    
    /** 強制終了フラグ */
    protected volatile boolean mFourceEndFlg = false;
    
    /** セマフォ獲得モニタ */
    protected transient SynchronizeMonitor getMonitor = new WaitSynchronizeMonitor();
    
    /** セマフォ獲得スレッド集合 */
    protected transient ConcurrentMap usedThreads = new ConcurrentHashMap();
    
    /** セマフォ獲得スレッド集合 */
    protected transient ConcurrentMap threadTasks = new ConcurrentHashMap();
    
    /** 無限獲得待ちスレッドSleep時間[ms] */
    protected long sleepTime = 10000;
    
    /** リソース監視間隔 */
    protected long checkInterval = -1;
    
    /** リソース監視タスク */
    protected transient ResourceChecker checker;
    
    /** 最大リソース使用数実績 */
    protected int maxUsedResource;
    
    /** 最大リソース獲得待ち数実績 */
    protected int maxWaitedCount;
    
    /** 強制開放タイマー */
    protected transient Timer forceFreeTimer = new Timer(true);
    
    protected boolean isThreadBinding = true;
    
    // SemaphoreのJavaDoc
    public boolean getResource(){
        return this.getResource(-1L) ;
    }
    
    // SemaphoreのJavaDoc
    public boolean getResource(int maxWaitCount){
        return getResource(-1L, maxWaitCount) ;
    }
    
    // SemaphoreのJavaDoc
    public boolean getResource(long timeOutMiliSecond){
        return getResource(timeOutMiliSecond, -1);
    }
    
    // SemaphoreのJavaDoc
    public boolean getResource(long timeOutMiliSecond, int maxWaitCount){
        return getResource(timeOutMiliSecond, maxWaitCount, -1);
    }
    
    // SemaphoreのJavaDoc
    public boolean getResource(
        long timeOutMiliSecond,
        int maxWaitCount,
        long forceFreeMiliSecond
    ){
        final Thread current = Thread.currentThread();
        if((mResourceCnt <= 0 && maxWaitCount > 0 && getMonitor.getWaitCount() > maxWaitCount)
            || mFourceEndFlg){
            return false;
        }
        getMonitor.initMonitor();
        long timeOutMs = -1 ;
        if(timeOutMiliSecond >= 0){
            timeOutMs = timeOutMiliSecond;
        }
        long processTime = 0;
        try{
            // 強制終了でない場合
            while(!mFourceEndFlg){
                // リソースが余っている場合
                if(mResourceCnt > 0){
                    boolean isGet = false;
                    synchronized(getMonitor){
                        if(mResourceCnt > 0){
                            // リソースを獲得する
                            mResourceCnt--;
                            getMonitor.releaseMonitor();
                            final int nowUsed = mInitialResource - mResourceCnt;
                            if(nowUsed > maxUsedResource){
                                maxUsedResource = nowUsed;
                            }
                            isGet = true;
                        }
                    }
                    if(isGet){
                        if(isThreadBinding){
                            // 強制開放時間が指定されている場合
                            TimerTask task = null;
                            if(forceFreeMiliSecond > 0){
                                
                                // 強制開放タスクをタイマーに登録する
                                task = new ForceFreeTimerTask(current);
                                forceFreeTimer.schedule(task, forceFreeMiliSecond);
                            }
                            
                            // リソース使用中スレッドに登録する
                            usedThreads.put(current, current);
                            
                            // タスク管理にタスクを登録する
                            if(threadTasks.containsKey(current)){
                                final Object tasks = threadTasks.get(current);
                                List taskList = null;
                                if(tasks instanceof List){
                                    taskList = (List)tasks;
                                }else{
                                    taskList = new ArrayList();
                                    threadTasks.put(current, taskList);
                                }
                                taskList.add(task);
                            }else{
                                threadTasks.put(current, task);
                            }
                        }
                        if(mResourceCnt > 0 && getMonitor.isWait()){
                            getMonitor.notifyMonitor();
                        }
                        return true;
                    }
                }
                long proc = 0;
                synchronized(current){
                    // リソースが余っていない場合
                    // または、このスレッドよりも前に待っていたスレッドがいる場合
                    
                    // 強制終了またはタイムアウトの場合
                    if(mFourceEndFlg || (timeOutMs >= 0 && timeOutMs <= processTime)){
                        break;
                    }
                    
                    // タイムアウト指定がある場合は、タイムアウトまでsleepする
                    // タイムアウト指定がない場合は、sleepTime分sleepしてみる
                    if(timeOutMs >= 0){
                        proc = System.currentTimeMillis();
                    }
                }
                int priority = current.getPriority();
                try{
                    if(mResourceCnt <= 0){
                        if(priority < Thread.MAX_PRIORITY){
                            try{
                                current.setPriority(priority + 1);
                            }catch(SecurityException e){
                            }
                        }
                        long curSleepTime = timeOutMs >= 0 ? timeOutMs - processTime : sleepTime;
                        if(curSleepTime > 0 && mResourceCnt <= 0){
                            final int nowWaited = getMonitor.getWaitCount() + 1;
                            if(nowWaited > maxWaitedCount){
                                maxWaitedCount = nowWaited;
                            }
                            getMonitor.waitMonitor(curSleepTime);
                        }
                    }
                }catch(InterruptedException e){
                    if(!getMonitor.isNotify()){
                        return false;
                    }
                }finally{
                    try{
                        current.setPriority(priority);
                    }catch(SecurityException e){
                    }
                }
                if(timeOutMs >= 0){
                    proc = System.currentTimeMillis() - proc;
                    processTime += proc;
                }
            }
            
            // 強制終了またはタイムアウトの場合
            return false;
        }finally{
            getMonitor.releaseMonitor();
        }
    }
    
    // SemaphoreのJavaDoc
    public void freeResource(){
        freeResource(Thread.currentThread());
    }
    
    protected void freeResource(Thread usedThread){
        boolean isUsed = false;
        if(isThreadBinding){
            synchronized(usedThread){
                if(usedThreads.containsKey(usedThread)){
                    isUsed = true;
                    final Object tasks = threadTasks.get(usedThread);
                    if(tasks instanceof List){
                        final List taskList = (List)tasks;
                        final TimerTask task = (TimerTask)taskList.remove(0);
                        if(task != null){
                            task.cancel();
                        }
                        if(taskList.size() == 0){
                            threadTasks.remove(usedThread);
                            usedThreads.remove(usedThread);
                        }
                    }else{
                        final TimerTask task = (TimerTask)tasks;
                        if(task != null){
                            task.cancel();
                        }
                        threadTasks.remove(usedThread);
                        usedThreads.remove(usedThread);
                    }
                }
            }
        }
        synchronized(getMonitor){
            if((isThreadBinding && isUsed || !isThreadBinding) && mResourceCnt < mInitialResource){
                if(mResourceCnt < mInitialResource){
                    mResourceCnt++;
                }
            }
        }
        if(mFourceEndFlg || mResourceCnt > 0){
            getMonitor.notifyMonitor();
        }
    }
    
    // SemaphoreのJavaDoc
    public int getResourceCapacity(){
        return mInitialResource;
    }
    
    // SemaphoreのJavaDoc
    public void setResourceCapacity(int capa) {
        if(mInitialResource == -1){
            mInitialResource = capa ;
            mResourceCnt = capa ;
        }
    }
    
    // SemaphoreのJavaDoc
    public int getResourceRemain() {
        return mResourceCnt;
    }
    
    // SemaphoreのJavaDoc
    public int getWaitingCount(){
        return getMonitor.getWaitCount();
    }
    
    /**
     * セマフォに対して無限取得待ちをするスレッドがsleepする時間を設定する。<p>
     * 自分がセマフォ待ちの先頭でない場合は、再びsleepする。<br>
     * デフォルトは、10秒。
     *
     * @param millis セマフォに対して無限取得待ちをするスレッドがsleepする時間[ms]
     */
    public void setSleepTime(long millis){
        sleepTime = millis;
    }
    
    // SemaphoreのJavaDoc
    public long getSleepTime(){
        return sleepTime;
    }
    
    // SemaphoreのJavaDoc
    public void setCheckInterval(long millis){
        checkInterval = millis;
    }
    
    // SemaphoreのJavaDoc
    public long getCheckInterval(){
        return checkInterval;
    }
    
    // SemaphoreのJavaDoc
    public synchronized void release(){
        if(checker != null){
            checker.isStop = true;
            checker = null;
        }
        mFourceEndFlg = true;
        while(getMonitor.isWait()){
            getMonitor.notifyMonitor();
        }
        while(usedThreads.size() != 0){
            Object[] threads = usedThreads.keySet().toArray();
            for(int i = 0; i < threads.length; i++){
                freeResource((Thread)threads[i]);
            }
        }
        mResourceCnt = mInitialResource;
        forceFreeTimer.cancel();
        forceFreeTimer = new Timer(true);
    }
    
    // SemaphoreのJavaDoc
    public synchronized void accept(){
        mFourceEndFlg = false;
        if(checkInterval > 0){
            checker = new ResourceChecker();
            checker.start();
        }
    }
    
    // SemaphoreのJavaDoc
    public int getMaxUsedResource(){
        return maxUsedResource;
    }
    
    // SemaphoreのJavaDoc
    public int getMaxWaitedCount(){
        return maxWaitedCount;
    }
    
    // Semaphore のJavaDoc
    public void setThreadBinding(boolean isBinding){
        isThreadBinding = isBinding;
    }
    
    // Semaphore のJavaDoc
    public boolean isThreadBinding(){
        return isThreadBinding;
    }
    
    /**
     * デシリアライズを行う。<p>
     *
     * @param in デシリアライズの元情報となるストリーム
     * @exception IOException 読み込みに失敗した場合
     * @exception ClassNotFoundException デシリアライズしようとしたオブジェクトのクラスが見つからない場合
     */
    private void readObject(java.io.ObjectInputStream in)
     throws java.io.IOException, ClassNotFoundException{
        in.defaultReadObject();
        getMonitor = new WaitSynchronizeMonitor();
        usedThreads = new ConcurrentHashMap();
        threadTasks = new ConcurrentHashMap();
        forceFreeTimer = new Timer(true);
    }
    
    protected class ForceFreeTimerTask extends TimerTask{
        
        protected Thread usedThread;
        
        public ForceFreeTimerTask(Thread usedThread){
            this.usedThread = usedThread;
        }
        
        public void run(){
            freeResource(usedThread);
        }
    }
    
    protected class ResourceChecker extends Thread{
        
        public boolean isStop;
        
        public ResourceChecker(){
            super("Nimbus SemaphoreResourceCheckDaemon");
            setDaemon(true);
        }
        
        public void run(){
            while(!isStop && checkInterval > 0){
                try{
                    Thread.sleep(checkInterval);
                }catch(InterruptedException e){}
                if(mResourceCnt > 0){
                    getMonitor.notifyMonitor();
                }
            }
        }
    }
}
